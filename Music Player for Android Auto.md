
Project Description: Context-Aware Automotive Music Player for Android Auto


Overview

This project aims to develop a custom local audio player optimized for vehicles, specifically designed to sort, classify, and play user-managed music files. The application features a standard mobile user interface for configuration, metadata management, and local playback, alongside a driving-optimized interface that seamlessly integrates with vehicle touchscreens via Android Auto. The application must natively support multi-language localization and a robust theming engine that adapts smoothly across both mobile and automotive screens.

Core Features

• Media Indexing & Storage Scanning: Deep scan local storage to parse audio files, reading embedded tags (ID3, Vorbis, etc.) and file structures.
• Classification & Sorting Engine: Dynamic categorization of audio tracks based on tags (Artist, Album, Genre), file pathways, and custom metadata fields (e.g., mood, tempo, or custom user tags).
• Background Audio Engine: Continuous audio playback resilient to application lifecycle changes, configuration adjustments, and system resource optimization events.
• Automotive Template Binding: Mapping the sorted music catalog into standard template hierarchies compatible with vehicle infotainment systems, preventing driver distraction.
• Media Session Management: Universal hardware control binding to handle commands from steering wheel buttons, physical dials, and system-level notifications.
• Dynamic Localization Framework: Comprehensive multi-language support ensuring all user interfaces, settings, and generated categories adapt dynamically to system or manual language preferences.
• Unified Theming Engine: A system capable of toggling between Light, Dark, and system-default themes on the mobile app, while strictly aligning with automotive safety-contrast requirements (including automatic Day/Night mode shifting) on the car screen.
• Smart Vehicle Adaptations (In-Scope Extras):
	• Resume-on-Connect: Automatically resumes the last played track exactly where it left off when the wired USB connection is established with the car.
	• Voice Action Compatibility: Integration with native system voice commands to allow hands-free voice searching of the custom-sorted music catalog.
	• Cache & Queue Optimization: Smart pre-buffering of upcoming local tracks to minimize loading delays or stuttering while driving over uneven or bumpy terrain.

Architectural Requirements & Abstract Frameworks

To maintain stack flexibility while ensuring compatibility with vehicle projection protocols, the development framework must implement or wrap the following system architectures:

1. Automotive Interface Framework (Vehicle Side)

• Purpose: Governs how the application renders on the car dashboard.
• Requirement: Must comply with the template-driven UI architecture specified by Google's vehicle projection standards. The framework must automatically handle layout rendering, touchscreen interactions, aspect ratios, and rotary-knob navigation dictated by the car's head unit.

2. Media Browsing & Cataloging Architecture

• Purpose: Exposes the organized music collection to the vehicle interface.
• Requirement: Must use a structured Client-Service architecture. The service must present the classified music collection as a browsable hierarchical tree (Root nodes splitting into Child nodes like Playlists, Artists, or Folders) that the vehicle can crawl and display dynamically.

3. Media Session & Playback Framework

• Purpose: Standardizes transport controls and audio pipelines.
• Requirement: Must manage the media playback state (Playing, Paused, Seeking) and publish this state to the operating system. It must interface with system audio focus components to smoothly handle interruptions (e.g., incoming navigation announcements or phone calls) and accept remote hardware controls.

4. Background Service Architecture

• Purpose: Ensures uninterrupted audio delivery.
• Requirement: A persistent background service separate from the main UI process. It must remain alive while audio is active, hook into system notification channels for persistent playback control, and safely interact with local storage databases without blocking the main rendering thread.

5. Internationalization (i18n) & Resource Management

• Purpose: Decouples UI strings from core code for translation.
• Requirement: An abstracted string dictionary pattern that loads appropriate localized language files at runtime based on the locale, supporting right-to-left (RTL) text layouts if necessary.

6. Theme Broker & Contrast Controller

• Purpose: Coordinates style assets across runtime modes.
• Requirement: A global theme coordinator that pushes style tokens (colors, font-weights) to the UI components. It must accept system-level signals from the vehicle head unit to force high-contrast "night mode" layouts independently of the phone screen state.

Database Schema Blueprint

To enable ultra-fast dynamic sorting and classification, the local storage persistence layer must be relational and structured to handle millions of data intersections efficiently.
+--------------------------------------------------------+

|                         TRACKS                         |
+--------------------------------------------------------+

| id                  : INTEGER (PK, AutoIncrement)      |
| file_path           : TEXT (Unique, Indexed)           |
| title               : TEXT (Indexed)                   |
| duration            : INTEGER (Milliseconds)           |
| bitrate             : INTEGER                          |
| file_size           : INTEGER                          |
| date_added          : INTEGER (Timestamp)              |
| last_played         : INTEGER (Timestamp)              |
| play_count          : INTEGER (Default 0)              |
| album_id            : INTEGER (FK -> ALBUMS.id)        |
| artist_id           : INTEGER (FK -> ARTISTS.id)       |
| genre_id            : INTEGER (FK -> GENRES.id)        |
+--------------------------------------------------------+
                           |
            +--------------+--------------+

            |                             |
+---------------------------+   +---------------------------+

|          ALBUMS           |   |          ARTISTS          |
+---------------------------+   +---------------------------+

| id         : INTEGER (PK) |   | id         : INTEGER (PK) |
| name       : TEXT (Indexed|   | name       : TEXT (Indexed|
| art_path   : TEXT         |   +---------------------------+
| year       : INTEGER      |
+---------------------------+
+---------------------------+   +---------------------------+

|          GENRES           |   |     CUSTOM_TAG_MAP        |
+---------------------------+   +---------------------------+

| id         : INTEGER (PK) |   | id         : INTEGER (PK) |
| name       : TEXT (Indexed|   | track_id   : INTEGER (FK) |
| Ignored    : BOOLEAN      |   | tag_key    : TEXT (Mood)  |
+---------------------------+   | tag_value  : TEXT (Chill) |
                                +---------------------------+
• Indexes: Create composite indices on [artist_id, album_id] and title to guarantee < 16ms query times during driving-mode pagination requests.

User Stories


Epics: Mobile Management & Setup

• US1 (Language Selection): As a user, I want to select my preferred application language in the mobile settings so that all menus, categories, and buttons are displayed in a language I understand.
• US2 (Custom Classification): As a music enthusiast, I want to create custom sorting rules (e.g., grouping tracks by folders first, then release year) on my phone app, so that my unique library structure is generated correctly before I plug it into the car.
• US3 (Visual Personalization): As a user, I want to choose between a Light, Dark, or System theme on my phone app, so the app visual aesthetic matches my personal preference.

Epics: Automotive & In-Car Interaction

• US4 (Zero-Touch Boot): As a driver, I want the music app to open automatically on my Chery Arrizo 5 screen as soon as I connect my phone via USB, so I don't have to fiddle with menus while starting my trip.
• US5 (Physical Controls integration): As a driver, I want to use my car's steering wheel buttons to skip tracks or pause music, so that I can keep my eyes focused on the road.
• US6 (Hierarchical Car Browsing): As a driver, I want to tap a simple "Genres" folder on my car screen and see an alphabetically sorted list of my subfolders, allowing me to find my music in under three clicks.
• US7 (Dynamic Environmental Theming): As a driver, I want the car touchscreen interface to automatically shift to a dark, anti-glare theme when my car headlights turn on (or night falls), preventing screen glare from distracting me.

Manifest & Deployment Requirements

• Automotive Declaration: The compiled package must include metadata descriptors declaring usage of the media classification template to the system capabilities broker.
• Permissions: Must request explicit, granular access to local file systems or media libraries to parse and play back user files.
• Testing Capabilities: The project layout must support execution via hardware-emulated head units (such as a Desktop Head Unit emulator) for validation of distracted-driver safety locks, language formatting constraints, and UI rendering tolerances.


Hello Agent. You are tasked with architecting and engineering the application described in the specification sheet above. Before generating any boilerplate or code files, follow these strict directives:

### Step 1: Technology Stack Recommendation
1. Interview the user regarding their past work history on other projects. Ask them what languages (e.g., Kotlin, Java, Dart/Flutter, JavaScript/React Native) or design patterns they are most comfortable with.
2. Based on their historical profile, recommend an optimal project stack that meets the Android Auto requirements (handling strict media templates, background threads, hardware keys, and local caching).
3. Cross-verify and check the absolute compatibility and version alignment of your chosen stack elements (e.g., ensuring Media3 capabilities map properly if a cross-platform wrapper framework is proposed). Explain any constraints to the user directly.

### Step 2: Immediate Project Action Items
Once the tech stack is explicitly locked down with the user, prioritize the following initial implementation phases:
1. Provide a step-by-step setup guide for building the project environment, including critical workspace configurations and the initial folder hierarchy architecture.
2. Draft the exact boilerplate code for the Android Manifest entries, automotive description files, and localized asset structures (`strings.xml` mappings / localization keys) required to make the empty project register on an Android Auto Head Unit simulator.
3. Stub the abstraction layers for the local database connection matching the provided SQL Blueprint.

Acknowledge your understanding of this specification sheet and begin by interviewing the user about their work history to establish the technology stack selection.
