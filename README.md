# Mactonish

A retro Java Swing desktop with built-in mini apps, fake system checks, startup
sound, and dramatic BSOD-style error screens.

## Features

- Finder with a split file browser, text preview/editor, and Trash support.
- Taskbar window switcher for open internal apps.
- Settings app for wallpaper color, startup sound, desktop password, and icon cleanup.
- P-Run, Terminal, Notepad, App Maker, File Edit, Music Edit, SSH Connect,
  Password Vault, Image Viewer, Paint, Reminders, Calculator, Clock, Sys Info,
  and Help.
- Fake BSOD triggers from menus, error paths, typed crash phrases, and the
  Emergency Panel.

## Login

The default desktop password is `soap`. The backup password is `chip`.
Changing the password in Settings stores it in the local Java preferences for
the current user.

## Run

```sh
./run.sh
```

Requires Java 17 or newer.

## BSOD Triggers

- Menu: `Mactonish -> Trigger BSOD`
- Desktop menu: `Trigger Taskbar BSOD`
- Finder menu: `Trigger Trash BSOD`
- Settings menu: `Trigger Settings BSOD`
- Emergency Panel commands: `function:settings-bsod`, `function:trash-bsod`,
  and `function:taskbar-bsod`

## Build macOS installer

```sh
chmod +x package-macos-pkg.sh
./package-macos-pkg.sh
```

The installer is written to `dist/Mactonish-1.5.3.pkg`. It installs `Mactonish.app` into `/Applications` and requires Java 17 or newer on the target Mac.
