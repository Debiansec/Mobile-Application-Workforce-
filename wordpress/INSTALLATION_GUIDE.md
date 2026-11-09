# PORTAL WFHSA — WordPress Installation & Elementor Kit Deployment Guide

## 1. Package Overview
The **Portal WFHSA** WordPress ecosystem is located in `/wordpress` and `/public/wordpress`:

1. **`portalwfhsa-theme.zip`**: The official responsive dark industrial portal theme for WorkForce Health SA and South African mining field operations, featuring native Elementor Kit support.
2. **`portalwfhsa-elementor-kit.zip`**: Complete Elementor Template Kit containing Header, Footer, Inspection Dashboard, 28-Page Single Assessment Dossier, and Location Selector templates.
3. **`portalwfhsa-elementor-widgets.zip`**: Native Elementor widgets addon plugin providing drag-and-drop compliance KPI metrics, checklist matrices, voice/GPS stampers, and remedial registers.
4. **`portalwfhsa-complete-suite.zip`** (Recommended): All-in-one consolidated plugin containing User Roles, 3-Step Location Selector, Inspections Engine (Voice + GPS), REST API sync endpoints, and PDF/Audit Report Generator.
5. **Modular Plugins**:
   - `portalwfhsa-sync.zip` (Dedicated Android Sync REST API Engine for `https://wfhsa.com/new/wp-json/portalwfhsa/v1/sync`)
   - `portalwfhsa-inspections.zip` (Core inspection checklist & 51-point scoring engine with compulsory remarks, voice-to-text dictation & GPS geotagging)
   - `portalwfhsa-user-roles.zip` (Field Assessor, Hygienist SAIOH, Mine Manager, SHE Rep roles)
   - `portalwfhsa-location-selector.zip` (3-step Customer, Site & Ablution Facility wizard)
   - `portalwfhsa-reports-pdf.zip` (28-page PDF report generator matching Master Template Assessment Ablution)
6. **`portalwfhsa-complete-package.zip`**: Master distribution bundle containing theme, Elementor kit, all plugins, documentation, and database seeds.

---

## 2. Installation Instructions (WordPress Admin)

### Step 1: Install & Activate the Theme
1. Navigate to your WordPress Admin dashboard (`https://wfhsa.com/new/wp-admin`).
2. Go to **Appearance > Themes > Add New > Upload Theme**.
3. Choose `portalwfhsa-theme.zip` and click **Install Now**.
4. Click **Activate**.

### Step 2: Install Elementor & Import Template Kit
1. Install and activate the **Elementor** plugin from the WordPress repository (if not already installed).
2. Go to **Plugins > Add New > Upload Plugin** and install `portalwfhsa-elementor-widgets.zip`.
3. In Elementor, go to **Tools > Import / Export Kit > Import a Template Kit**.
4. Upload `portalwfhsa-elementor-kit.zip` to apply global colors, typography, header, footer, and dashboard templates.

### Step 3: Install the Complete Suite Plugin
1. Go to **Plugins > Add New > Upload Plugin**.
2. Choose `portalwfhsa-complete-suite.zip`.
3. Click **Install Now** and **Activate Plugin**.

### Step 4: Configure Pretty Permalinks
1. Go to **Settings > Permalinks**.
2. Select **Post name** (`/%postname%/`).
3. Click **Save Changes**.

---

## 3. Server WP-CLI Installation (Fast Track)

```bash
cd /var/www/wfhsa.com/new/public_html

# Install and activate theme
wp theme install /path/to/portalwfhsa-theme.zip --activate

# Install and activate plugins
wp plugin install /path/to/portalwfhsa-complete-suite.zip --activate
wp plugin install /path/to/portalwfhsa-elementor-widgets.zip --activate

# Flush rewrite rules
wp rewrite flush
```

---

## 4. API Endpoints for Android Synchronization
- **Health Check**: `GET https://wfhsa.com/new/wp-json/portalwfhsa/v1/health`
- **Sync Endpoint**: `POST https://wfhsa.com/new/wp-json/portalwfhsa/v1/sync`
- **Checklist Questions**: `GET https://wfhsa.com/new/wp-json/portalwfhsa/v1/checklist/questions`
- **Submit Inspection**: `POST https://wfhsa.com/new/wp-json/portalwfhsa/v1/inspections/submit`
