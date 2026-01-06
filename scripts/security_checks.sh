#!/usr/bin/env bash
set -euo pipefail

echo "Running repository security checks..."
ROOT_DIR="$(git rev-parse --show-toplevel)"
cd "$ROOT_DIR"

# Patterns to flag
declare -a PATTERNS=(
  "getSharedPreferences\("
  "\.edit\(\)\.putString\("
  "android.util.Log\.d\("
  "android.util.Log\.e\("
  "println\("
  "req.url"
  "req.url\.encodedPath"
)

EXIT_CODE=0

echo "Searching for risky patterns..."
for p in "${PATTERNS[@]}"; do
  echo "Checking pattern: $p"
  # Search in Kotlin/Java files only
  matches=$(grep -RIn --exclude-dir=.git --exclude-dir=build --exclude-dir=.gradle --include=*.kt --include=*.java -e "$p" || true)
  if [[ -n "$matches" ]]; then
    echo "Found matches for pattern '$p':"
    echo "$matches"
    # Filter out allowed places: TokenManager (EncryptedSharedPreferences) and AppLog wrapper
    # If any match is in TokenManager.kt or AppLog.kt or contains 'EncryptedSharedPreferences' nearby, ignore
    while IFS= read -r line; do
      file=$(echo "$line" | cut -d: -f1)
      if [[ "$file" == *TokenManager.kt || "$file" == *AppLog.kt ]]; then
        echo " - Allowed match in $file"
        continue
      fi
      # check if file contains EncryptedSharedPreferences
      if grep -q "EncryptedSharedPreferences" "$file" 2>/dev/null; then
        echo " - Allowed (uses EncryptedSharedPreferences) in $file"
        continue
      fi
      echo " - Potential issue in: $line"
      EXIT_CODE=1
    done <<< "$matches"
  fi
done

# Additional check: raw HttpLoggingInterceptor usage
http_logging=$(grep -RIn --exclude-dir=.git --exclude-dir=build --exclude-dir=.gradle -e "HttpLoggingInterceptor" --include=*.kt --include=*.java || true)
if [[ -n "$http_logging" ]]; then
  echo "Found HttpLoggingInterceptor usage (ensure Authorization is redacted):"
  echo "$http_logging"
  EXIT_CODE=1
fi

if [[ $EXIT_CODE -ne 0 ]]; then
  echo "Security checks failed. Please review the reported files and ensure sensitive data is stored/handled securely." >&2
  exit $EXIT_CODE
fi

echo "All security checks passed."
