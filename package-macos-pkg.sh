#!/usr/bin/env sh
set -eu

export COPYFILE_DISABLE=1

APP_NAME="Mactonish"
DISPLAY_NAME="Mactonish System"
VERSION="1.5.3"
IDENTIFIER="com.elia.mactonish"

BUILD_DIR="build/pkg"
CLASSES_DIR="$BUILD_DIR/classes"
APP_DIR="$BUILD_DIR/root/Applications/$APP_NAME.app"
CONTENTS_DIR="$APP_DIR/Contents"
MACOS_DIR="$CONTENTS_DIR/MacOS"
RESOURCES_DIR="$CONTENTS_DIR/Resources"
DIST_DIR="dist"
JAR_NAME="$APP_NAME.jar"
PKG_PATH="$DIST_DIR/$APP_NAME-$VERSION.pkg"

rm -rf "$BUILD_DIR" "$PKG_PATH"
mkdir -p "$CLASSES_DIR" "$MACOS_DIR" "$RESOURCES_DIR" "$DIST_DIR"

javac --release 17 -d "$CLASSES_DIR" src/Main.java
jar --create --file "$RESOURCES_DIR/$JAR_NAME" --main-class Main -C "$CLASSES_DIR" .

cat > "$MACOS_DIR/$APP_NAME" <<EOF
#!/usr/bin/env sh
set -eu

APP_DIR=\$(CDPATH= cd -- "\$(dirname -- "\$0")/.." && pwd)

if ! command -v java >/dev/null 2>&1; then
  osascript -e 'display dialog "Mactonish System requires Java 17 or newer." buttons {"OK"} default button "OK" with icon caution' >/dev/null 2>&1 || true
  exit 1
fi

exec java -Xdock:name="$DISPLAY_NAME" -jar "\$APP_DIR/Resources/$JAR_NAME" "\$@"
EOF
chmod 755 "$MACOS_DIR/$APP_NAME"

cat > "$CONTENTS_DIR/Info.plist" <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "https://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
  <key>CFBundleDevelopmentRegion</key>
  <string>en</string>
  <key>CFBundleDisplayName</key>
  <string>$DISPLAY_NAME</string>
  <key>CFBundleExecutable</key>
  <string>$APP_NAME</string>
  <key>CFBundleIdentifier</key>
  <string>$IDENTIFIER</string>
  <key>CFBundleInfoDictionaryVersion</key>
  <string>6.0</string>
  <key>CFBundleName</key>
  <string>$DISPLAY_NAME</string>
  <key>CFBundlePackageType</key>
  <string>APPL</string>
  <key>CFBundleShortVersionString</key>
  <string>$VERSION</string>
  <key>CFBundleVersion</key>
  <string>$VERSION</string>
  <key>LSMinimumSystemVersion</key>
  <string>10.13</string>
  <key>NSHighResolutionCapable</key>
  <true/>
</dict>
</plist>
EOF

xattr -cr "$BUILD_DIR/root"

pkgbuild \
  --root "$BUILD_DIR/root" \
  --identifier "$IDENTIFIER" \
  --version "$VERSION" \
  --install-location "/" \
  --filter '(^|/)\._.*' \
  --filter '/\.DS_Store$' \
  --filter '/\.svn($|/)' \
  --filter '/CVS($|/)' \
  "$PKG_PATH"

printf 'Built %s\n' "$PKG_PATH"
