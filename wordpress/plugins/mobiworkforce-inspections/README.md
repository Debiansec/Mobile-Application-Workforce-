# MobiWorkforce — Start New Inspection Engine (v1.3.0)

## Overview
The **MobiWorkforce Inspection Engine** plugin powers the digital hygiene audit workflow across WorkForce Health SA and MobiWorkforce platforms. It provides a 51-point ablution facilities checklist matrix compliant with DMR (Department of Mineral Resources) occupational hygiene regulations.

## New in v1.3.0
- **Voice-to-Text Dictation vs Typing Mode Toggle**:
  - Inspectors can switch between keyboard typing and hands-free voice dictation on every single question finding.
  - Built-in Web Speech API (`SpeechRecognition`) with pulsing microphone animation and live speech transcription.
- **GPS Coordinates Geotagging**:
  - Capture precise GPS latitude, longitude, and accuracy radius directly into inspection findings.
  - Generates verifiable geotag badges and direct Google Maps inspection location links.
  - Master telemetry bar to acquire live satellite coordinates or bulk-stamp all findings.
- **Tactile Preset Observation Chips**:
  - 1-tap insert of common occupational hygiene defects (Scum/limescale, leaking valves, soap dispenser depleted, chemical SDS missing, floor drain blockage, sanitized & compliant).
- **REST Endpoints for Android Companion App**:
  - `GET /wp-json/mobiworkforce/v1/checklist/questions`
  - `POST /wp-json/mobiworkforce/v1/inspections/submit`
  - `GET /wp-json/mobiworkforce/v1/inspections/findings`

## Shortcode
Add `[mobiworkforce_new_inspection]` or `[wfhsa_new_inspection]` to any page or post to render the full inspection checklist.
