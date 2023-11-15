# devenv-intelij

![Build](https://github.com/shyim/devenv-intelij/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)

<!-- Plugin description -->
This plugin adds some utilities to simplify the Devenv.sh configuration for Intelij IDEs. 

Right now it supports only PHP as language.

- Detect Devenv project and configure a PHP interpreter
  - Pcov or XDebug executables are preferred, to be used in Test runner
- Automatically injection of Devenv environment variables into the IDE like Test runner
<!-- Plugin description end -->

## Installation

- Using IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "devenv-intelij"</kbd> >
  <kbd>Install Plugin</kbd>
  
- Manually:

  Download the [latest release](https://github.com/shyim/devenv-intelij/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>
