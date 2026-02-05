# ProductivityAssistant

Android prototype for a multi-user Pomodoro timer with customizable focus cycles and on-device data persistence.

> ⚠️ This project is a **functional prototype** built for academic and learning purposes.  
> It focuses on demonstrating core Pomodoro timer logic and local user session handling rather than production-ready features.

## Project Overview

ProductivityAssistant is an Android application prototype designed to help students improve focus and time management using the Pomodoro technique.

The app allows users to:
- Create local user profiles on a single device
- Run Pomodoro-based focus sessions with configurable work and break cycles
- Track session progress and productivity summaries

## Core Concept

The application is built around a customizable Pomodoro workflow.  
Users can define their own focus cycles (work duration, short breaks, long breaks, and repetition count) instead of being limited to a fixed Pomodoro preset.

## Features

### User & Session Handling
- ✔ Local user profile creation and login (single-device prototype)
- ✔ Session persistence per user on the same device
- ✖ Cross-device accounts or cloud synchronization

### Pomodoro Timer
- ✔ Pomodoro-based countdown timer
- ✔ Automatic transitions between work, short break, and long break
- ✔ Customizable cycle configuration (durations and repetitions)
- ✔ Timer reset and session restart

### Productivity Tracking
- ✔ Session completion tracking
- ✔ Basic productivity summary per user

### General
- ◐ Minimal error handling
- ✔ All core screens fully wired and functional

## Screenshots

### User Setup

| Sign Up | Load Preset |
|--------|-------------|
| ![Sign Up](screenshots/SignUp.png) | ![Load Preset](screenshots/LoadPresetSession.png) |

---

### Timer Flow

| Focus Session | Short Break | Long Break |
|---------------|-------------|------------|
| ![Focus](screenshots/ProductivityAssistant.png) | ![Short Break](screenshots/MainPage-ShortBreak.png) | ![Long Break](screenshots/MainPage-LongBreak.png) |

---

### Customization & Control

| Add Session | Settings | Reset |
|-------------|----------|-------|
| ![Add Session](screenshots/AddCustomizeNewSession.png) | ![Settings](screenshots/Settings.png) | ![Reset](screenshots/ResetTimer.png) |

---

### Summary

| Productivity Summary |
|----------------------|
| ![Summary](screenshots/ProductivitySummary.png) |

## Technology Stack

- **Language:** Java
- **Platform:** Android SDK
- **UI:** XML layouts
- **Local Storage:** On-device persistence (e.g., SharedPreferences or local database)
- **Build System:** Gradle

## Architecture Overview

The application is structured around clear separation between user interface logic, timer control logic, and data persistence.

- **Activities** manage user interaction, navigation, and screen state.
- **Timer logic** controls countdown behavior and Pomodoro cycle transitions.
- **Local storage** is used to persist user profiles, timer configurations, and session progress on the device.

The design prioritizes deterministic timer behavior and simplicity over architectural complexity.

## Data Handling

- User profiles and timer configurations are stored locally on the device.
- Session progress and productivity summaries are persisted per user.
- No cloud services or external servers are used in this prototype.


