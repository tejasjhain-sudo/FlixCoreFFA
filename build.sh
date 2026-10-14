#!/bin/bash
set -e

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
VERSION="1.0.6"
REPO="tejasjhain-sudo/FlixCoreFFA"

# Locate Java 21
if [ -z "$JAVA_HOME" ]; then
    if [ -d "/Users/tejas/Library/Application Support/PrismLauncher/java/java-runtime-delta" ]; then
        export JAVA_HOME="/Users/tejas/Library/Application Support/PrismLauncher/java/java-runtime-delta"
    fi
fi

JAVAC="$JAVA_HOME/bin/javac"
JAVA="$JAVA_HOME/bin/java"

if [ ! -f "$JAVAC" ]; then
    JAVAC="javac"
    JAVA="java"
fi

echo "==========================================="
echo "   FlixCore FFA Advanced Build Tool"
echo "==========================================="
echo "Using Java: $("$JAVA" -version 2>&1 | head -n 1)"

M2_REPO="$HOME/.m2/repository"
PAPER_JAR="$M2_REPO/io/papermc/paper/paper-api/1.21.4-R0.1-SNAPSHOT/paper-api-1.21.4-R0.1-SNAPSHOT.jar"
BUNGEE_JAR="$M2_REPO/net/md-5/bungeecord-chat/1.20-R0.2/bungeecord-chat-1.20-R0.2.jar"
PAPI_JAR="$M2_REPO/me/clip/placeholderapi/2.11.6/placeholderapi-2.11.6.jar"
HIKARI_JAR="$M2_REPO/com/zaxxer/HikariCP/5.1.0/HikariCP-5.1.0.jar"
VAULT_JAR="$M2_REPO/com/github/MilkBowl/VaultAPI/1.7/VaultAPI-1.7.jar"
GSON_JAR="$M2_REPO/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar"
ADV_API="$M2_REPO/net/kyori/adventure-api/4.17.0/adventure-api-4.17.0.jar"
ADV_KEY="$M2_REPO/net/kyori/adventure-key/4.17.0/adventure-key-4.17.0.jar"
ADV_PLAIN="$M2_REPO/net/kyori/adventure-text-serializer-plain/4.17.0/adventure-text-serializer-plain-4.17.0.jar"
EXAM_JAR="$M2_REPO/net/kyori/examination-api/1.3.0/examination-api-1.3.0.jar"
ASM_JAR="$M2_REPO/org/ow2/asm/asm/9.7.1/asm-9.7.1.jar"
ASM_COMMONS_JAR="$M2_REPO/org/ow2/asm/asm-commons/9.7.1/asm-commons-9.7.1.jar"
ASM_TREE_JAR="$M2_REPO/org/ow2/asm/asm-tree/9.7.1/asm-tree-9.7.1.jar"

SOURCE_JAR="$DIR/TwoCubeFFA-1.0.6.jar"

if [ ! -f "$SOURCE_JAR" ]; then
    echo "ERROR: Could not find base jar TwoCubeFFA-1.0.6.jar"
    exit 1
fi

BUILD_DIR="$DIR/target"
CLASSES_DIR="$BUILD_DIR/classes"
mkdir -p "$CLASSES_DIR"

echo "[1/4] Preparing remapped base classes (in.twocube.ffa -> club.aspvp.ffa)..."
"$JAVAC" -cp "$ASM_JAR:$ASM_COMMONS_JAR" -d "$BUILD_DIR" "$DIR/src/main/java/club/aspvp/ffa/build/RemapBase.java"
"$JAVA" -cp "$BUILD_DIR:$ASM_JAR:$ASM_COMMONS_JAR" club.aspvp.ffa.build.RemapBase "$SOURCE_JAR" "$BUILD_DIR/remapped-base.jar"

REMAPPED_BASE="$BUILD_DIR/remapped-base.jar"
CP="$REMAPPED_BASE:$CLASSES_DIR:$ASM_JAR:$ASM_COMMONS_JAR:$ASM_TREE_JAR:$PAPER_JAR:$BUNGEE_JAR:$PAPI_JAR:$HIKARI_JAR:$VAULT_JAR:$GSON_JAR:$ADV_API:$ADV_KEY:$ADV_PLAIN:$EXAM_JAR"

echo "[2/4] Compiling modified source classes (club.aspvp)..."
"$JAVAC" -cp "$CP" -d "$CLASSES_DIR" -encoding UTF-8 \
  "$DIR/src/main/java/club/aspvp/license/LicenseClient.java" \
  "$DIR/src/main/java/club/aspvp/ffa/updater/PluginUpdater.java" \
  "$DIR/src/main/java/club/aspvp/ffa/api/FlixCoreFFAAPI.java" \
  "$DIR/src/main/java/club/aspvp/ffa/internal/ab2c.java" \
  "$DIR/src/main/java/club/aspvp/ffa/internal/z.java" \
  "$DIR/src/main/java/club/aspvp/ffa/internal/k.java" \
  "$DIR/src/main/java/club/aspvp/ffa/internal/n.java" \
  "$DIR/src/main/java/club/aspvp/ffa/internal/p6d2.java" \
  "$DIR/src/main/java/club/aspvp/ffa/internal/v.java" \
  "$DIR/src/main/java/club/aspvp/ffa/internal/s.java" \
  "$DIR/src/main/java/club/aspvp/ffa/internal/o3w8.java" \
  "$DIR/src/main/java/club/aspvp/ffa/internal/q.java" \
  "$DIR/src/main/java/club/aspvp/ffa/internal/n2x6.java" \
  "$DIR/src/main/java/club/aspvp/ffa/internal/w7k3.java" \
  "$DIR/src/main/java/club/aspvp/ffa/internal/v8m5.java" \
  "$DIR/src/main/java/club/aspvp/ffa/TwoCubeFFA.java" \
  "$DIR/src/main/java/club/aspvp/ffa/FlixCoreFFA.java" \
  "$DIR/src/main/java/club/aspvp/ffa/hook/FfaPlaceholderExpansion.java" \
  "$DIR/src/main/java/club/aspvp/ffa/build/FlixFFATransformer.java"

echo "[3/4] Transforming, sanitizing bytecode (Layer 1) & generating encrypted payload (Layer 4)..."
"$JAVA" -cp "$CLASSES_DIR:$PAPER_JAR:$ASM_JAR:$ASM_COMMONS_JAR:$ASM_TREE_JAR" club.aspvp.ffa.build.FlixFFATransformer \
  "$SOURCE_JAR" \
  "$CLASSES_DIR" \
  "$DIR/src/main/resources" \
  "$BUILD_DIR/FlixCoreFFA-$VERSION.jar"

echo "[4/4] Finalizing output..."
cp "$BUILD_DIR/FlixCoreFFA-$VERSION.jar" "$DIR/FlixCoreFFA-$VERSION.jar"
cp "$BUILD_DIR/core.bin" "$DIR/core.bin"
cp "$BUILD_DIR/FlixCoreFFA-$VERSION.jar" "/Users/tejas/Desktop/FlixCoreFFA-$VERSION.jar" 2>/dev/null || true
cp "$BUILD_DIR/FlixCoreFFA-$VERSION.jar" "/Users/tejas/Desktop/FlixCoreFFA.jar" 2>/dev/null || true
cp "$BUILD_DIR/core.bin" "/Users/tejas/Desktop/core.bin" 2>/dev/null || true

# If --release flag is provided or RELEASE=1, create GitHub Release and upload core.bin
if [ "$1" == "--release" ] || [ "$RELEASE" == "1" ]; then
    echo "==========================================="
    echo "   Publishing Encrypted Release to GitHub..."
    echo "==========================================="
    GH_TOKEN="${GITHUB_TOKEN:-}"
    if [ -z "$GH_TOKEN" ] && [ -f "$DIR/.gh_token" ]; then
        GH_TOKEN="$(tr -d '[:space:]' < "$DIR/.gh_token")"
    fi

    if [ -z "$GH_TOKEN" ]; then
        echo "ERROR: No GitHub token found. Set GITHUB_TOKEN or place in .gh_token"
        exit 1
    fi

    TAG="v$VERSION"
    echo "Target Release: $TAG on $REPO"

    RELEASE_DATA=$(curl -s -H "Authorization: token $GH_TOKEN" "https://api.github.com/repos/$REPO/releases/tags/$TAG")
    RELEASE_ID=$(echo "$RELEASE_DATA" | python3 -c "import sys, json; print(json.load(sys.stdin).get('id', ''))" 2>/dev/null || true)

    if [ -z "$RELEASE_ID" ] || [ "$RELEASE_ID" == "None" ] || [ "$RELEASE_ID" == "null" ]; then
        echo "Creating release $TAG..."
        RELEASE_DATA=$(curl -s -X POST \
            -H "Authorization: token $GH_TOKEN" \
            -H "Accept: application/vnd.github+json" \
            "https://api.github.com/repos/$REPO/releases" \
            -d "{\"tag_name\":\"$TAG\",\"target_commitish\":\"main\",\"name\":\"FlixCore FFA $TAG\",\"body\":\"Automated encrypted release payload for FlixCore FFA.\\nProtected with AES-128 binary encryption.\",\"draft\":false,\"prerelease\":false}")
        RELEASE_ID=$(echo "$RELEASE_DATA" | python3 -c "import sys, json; print(json.load(sys.stdin).get('id', ''))" 2>/dev/null || true)
    fi

    echo "Release ID: $RELEASE_ID"

    # Delete existing core.bin asset if already present
    ASSETS_DATA=$(curl -s -H "Authorization: token $GH_TOKEN" "https://api.github.com/repos/$REPO/releases/$RELEASE_ID/assets")
    OLD_ASSET_ID=$(echo "$ASSETS_DATA" | python3 -c "
import sys, json
for a in json.load(sys.stdin):
    if a.get('name') == 'core.bin':
        print(a.get('id', ''))
        break
" 2>/dev/null || true)

    if [ -n "$OLD_ASSET_ID" ] && [ "$OLD_ASSET_ID" != "None" ]; then
        echo "Replacing existing release asset ID $OLD_ASSET_ID..."
        curl -s -X DELETE -H "Authorization: token $GH_TOKEN" "https://api.github.com/repos/$REPO/releases/assets/$OLD_ASSET_ID"
    fi

    echo "Uploading encrypted payload (core.bin)..."
    UPLOAD_RES=$(curl -s -X POST \
        -H "Authorization: token $GH_TOKEN" \
        -H "Content-Type: application/octet-stream" \
        "https://uploads.github.com/repos/$REPO/releases/$RELEASE_ID/assets?name=core.bin" \
        --data-binary "@$BUILD_DIR/core.bin")

    DOWNLOAD_URL=$(echo "$UPLOAD_RES" | python3 -c "import sys, json; print(json.load(sys.stdin).get('browser_download_url', ''))" 2>/dev/null || true)
    echo "Release published successfully!"
    echo "Download URL: $DOWNLOAD_URL"
fi

echo "==========================================="
echo "   BUILD SUCCESSFUL!"
echo "   Protected Jar: $DIR/FlixCoreFFA-$VERSION.jar"
echo "   Encrypted Payload: $DIR/core.bin"
echo "==========================================="
