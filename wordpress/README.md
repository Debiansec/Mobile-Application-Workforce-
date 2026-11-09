# WFHSA WEB — WordPress Enterprise Platform Package

This repository contains the complete production-grade WordPress theme and plugin suite for the **WFHSA Web & WorkForce Health SA Occupational Hygiene Digital Platform**, connecting field assessors at South African mining operations (e.g., Letaba Crushers, Tzaneen) directly to `https://wfhsa.com/new/`.

---

## Architecture Highlights
- **Bi-Directional Mobile Sync:** High-performance REST API integration with the Android Kotlin/Compose app via `/wp-json/wfhsaweb/v1/sync` (with backward compatibility at `/wp-json/mobiworkforce/v1/sync` and `/wp-json/wfhsa/v1/sync`), supporting offline queuing, automatic retry, CameraX photo evidence attachments, and Room database persistence.
- **Voice-to-Text & Typing Comment Toggle:** Seamless hands-free voice dictation with real-time speech wave animation and 1-tap tactile preset hygiene observation chips.
- **GPS Coordinates Geotagging:** High-accuracy GPS stamping (latitude, longitude, accuracy radius, and direct Google Maps verification links) for every inspection finding.
- **51-Question Hygiene Module:** Authoritative digitisation of the Monthly Ablution Facilities Checklist (Male), categorized across Basins, Showers, Toilets, Urinals, Lockers, and General Sanitation/Chemicals.
- **Role-Based Workflow:** Distinct capabilities for:
  - `wfhsaweb_field_assessor`: Mobile data capture, voice-to-text notes, photo evidence with GPS stamps, draft saving.
  - `wfhsaweb_hygienist`: SAIOH registered oversight, corrective action review, audit approval.
  - `wfhsaweb_mine_manager`: Mine client counter-signing, compliance tracking, risk acknowledgment.
  - `wfhsaweb_system_admin`: Platform administration, customer/site registry, template management.
- **Local & Cloud PDF Reporting:** Generates print-ready formal Occupational Hygiene Compliance Certificates with score gauge, non-compliance matrix, and digital signature stamps.

---

## Directory & Package Structure
```
wordpress/
├── INSTALLATION_GUIDE.md           # Step-by-step administrator setup manual
├── README.md                       # Architecture and package reference
├── sample-data.sql                 # MySQL starter seed data
├── wfhsa-sample-data.json          # JSON structured demo records
├── wfhsaweb-complete-package.zip   # Master distribution archive (Theme + Plugins + Guides)
├── wfhsaweb-theme.zip              # WFHSA Web dark responsive theme archive
├── wfhsaweb-complete-suite.zip     # All-in-one consolidated master plugin archive
├── wfhsaweb-sync.zip               # High-speed Android Sync REST API plugin
├── wfhsaweb-user-roles.zip         # User and role authorization management plugin
├── wfhsaweb-location-selector.zip  # 3-step Customer, Site & Ablution Facility wizard
├── wfhsaweb-inspections.zip        # 51-point Inspection checklist engine (Voice + GPS)
├── wfhsaweb-reports-pdf.zip        # Compliance Certificate & PDF report generator
├── plugins/
│   ├── wfhsaweb-complete-suite/    # Master all-in-one plugin source
│   ├── wfhsaweb-sync/              # Mobile REST API sync engine
│   ├── wfhsaweb-inspections/       # Core inspection engine (Voice-to-Text + GPS Geotags)
│   ├── wfhsaweb-location-selector/ # 3-Step Customer/Site/Facility wizard
│   ├── wfhsaweb-user-roles/        # Role and capability definitions
│   └── wfhsaweb-reports-pdf/       # Certificate & PDF report generator
└── themes/
    └── wfhsaweb/                   # Official WFHSA Web responsive theme source
```

For installation steps, see [INSTALLATION_GUIDE.md](./INSTALLATION_GUIDE.md).
