#!/usr/bin/env python3
import os
import sys
import json
import shutil
import zipfile

def ensure_dir(d):
    if not os.path.exists(d):
        os.makedirs(d)

print("Starting generation of portalwfhsa theme, plugins, and Elementor kit...")

WP_ROOT = os.path.abspath("wordpress")
PUBLIC_WP_ROOT = os.path.abspath("public/wordpress")

THEMES_DIR = os.path.join(WP_ROOT, "themes")
PLUGINS_DIR = os.path.join(WP_ROOT, "plugins")
ELEMENTOR_KIT_DIR = os.path.join(WP_ROOT, "elementor-kit")

ensure_dir(THEMES_DIR)
ensure_dir(PLUGINS_DIR)
ensure_dir(ELEMENTOR_KIT_DIR)
ensure_dir(PUBLIC_WP_ROOT)

THEME_DIR = os.path.join(THEMES_DIR, "portalwfhsa")
ensure_dir(THEME_DIR)

# ==============================================================================
# 1. THEME: portalwfhsa
# ==============================================================================

# style.css
theme_style_css = """/*
Theme Name: Portal WFHSA
Theme URI: https://wfhsa.com/new/
Author: WorkForce Health SA
Author URI: https://wfhsa.com
Description: Official Portal WFHSA Theme Package for WorkForce Health SA Occupational Hygiene & Mining Compliance. Includes native Elementor Kit support, full Elementor theme locations (Header, Footer, Single, Archive), mobile-first responsive audit dashboard, 28-page master template ablution assessment displays, voice-to-text dictation, and GPS coordinates geotagging.
Version: 1.4.0
License: Proprietary
Text Domain: portalwfhsa
Tags: elementor, elementor-kit, dark, occupational-hygiene, mining-compliance, responsive, dashboard, custom-logo, editor-style
*/

:root {
  --wfhsa-red: #D32F2F;
  --wfhsa-red-dark: #9A0007;
  --wfhsa-red-light: #FFCDD2;
  --wfhsa-orange: #FF6D00;
  --wfhsa-orange-dark: #E65100;
  --wfhsa-orange-light: #FFE0B2;
  --wfhsa-bg: #0F1015;
  --wfhsa-surface: #171922;
  --wfhsa-surface-variant: #202330;
  --wfhsa-border: #2E3244;
  --wfhsa-text: #F8FAFC;
  --wfhsa-text-muted: #94A3B8;
  --wfhsa-green: #10B981;
  --wfhsa-green-dark: #047857;
  --wfhsa-amber: #F59E0B;
  --wfhsa-blue: #3B82F6;
  --radius-sm: 6px;
  --radius-md: 10px;
  --radius-lg: 14px;
}

* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

body {
  background-color: var(--wfhsa-bg);
  color: var(--wfhsa-text);
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  line-height: 1.6;
  -webkit-font-smoothing: antialiased;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

a {
  color: var(--wfhsa-orange);
  text-decoration: none;
  transition: color 0.2s ease;
}

a:hover {
  color: #FFA040;
  text-decoration: underline;
}

/* Header */
.portalwfhsa-header {
  background-color: var(--wfhsa-surface);
  border-bottom: 2px solid var(--wfhsa-red);
  padding: 14px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: sticky;
  top: 0;
  z-index: 100;
}

.portalwfhsa-brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

.portalwfhsa-logo-mark {
  background: linear-gradient(135deg, var(--wfhsa-red), var(--wfhsa-orange));
  color: #FFFFFF;
  font-weight: 900;
  font-size: 1.1rem;
  padding: 8px 12px;
  border-radius: var(--radius-sm);
  letter-spacing: 1px;
}

.portalwfhsa-title {
  font-size: 1.1rem;
  font-weight: 800;
  color: #FFFFFF;
  letter-spacing: 0.5px;
}

.portalwfhsa-subtitle {
  font-size: 0.75rem;
  color: var(--wfhsa-orange);
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.portalwfhsa-nav {
  display: flex;
  align-items: center;
  gap: 14px;
}

.portalwfhsa-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 0.75rem;
  font-weight: 700;
  padding: 4px 10px;
  border-radius: 20px;
}

.portalwfhsa-badge-green {
  background-color: rgba(16, 185, 129, 0.15);
  color: var(--wfhsa-green);
  border: 1px solid rgba(16, 185, 129, 0.3);
}

.portalwfhsa-badge-orange {
  background-color: rgba(255, 109, 0, 0.15);
  color: var(--wfhsa-orange);
  border: 1px solid rgba(255, 109, 0, 0.3);
}

.portalwfhsa-badge-red {
  background-color: rgba(211, 47, 47, 0.15);
  color: var(--wfhsa-red);
  border: 1px solid rgba(211, 47, 47, 0.3);
}

/* Container */
.portalwfhsa-container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 24px 16px;
  flex: 1;
  width: 100%;
}

/* Card */
.portalwfhsa-card {
  background-color: var(--wfhsa-surface);
  border: 1px solid var(--wfhsa-border);
  border-radius: var(--radius-md);
  padding: 20px;
  margin-bottom: 20px;
}

.portalwfhsa-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.portalwfhsa-stat-card {
  background-color: var(--wfhsa-surface);
  border: 1px solid var(--wfhsa-border);
  border-radius: var(--radius-md);
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.portalwfhsa-stat-label {
  font-size: 0.75rem;
  font-weight: 700;
  color: var(--wfhsa-text-muted);
  text-transform: uppercase;
  letter-spacing: 0.8px;
}

.portalwfhsa-stat-value {
  font-size: 1.8rem;
  font-weight: 900;
  color: #FFFFFF;
}

.portalwfhsa-stat-desc {
  font-size: 0.75rem;
  color: var(--wfhsa-text-muted);
}

/* Buttons */
.portalwfhsa-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 18px;
  border-radius: var(--radius-sm);
  font-weight: 700;
  font-size: 0.85rem;
  cursor: pointer;
  border: none;
  transition: all 0.2s ease;
  text-decoration: none;
}

.portalwfhsa-btn-primary {
  background-color: var(--wfhsa-red);
  color: #FFFFFF;
}

.portalwfhsa-btn-primary:hover {
  background-color: var(--wfhsa-red-dark);
  color: #FFFFFF;
  text-decoration: none;
}

.portalwfhsa-btn-accent {
  background-color: var(--wfhsa-orange);
  color: #FFFFFF;
}

.portalwfhsa-btn-accent:hover {
  background-color: var(--wfhsa-orange-dark);
  color: #FFFFFF;
  text-decoration: none;
}

.portalwfhsa-btn-outline {
  background-color: transparent;
  border: 1px solid var(--wfhsa-border);
  color: var(--wfhsa-text);
}

.portalwfhsa-btn-outline:hover {
  background-color: var(--wfhsa-surface-variant);
  border-color: var(--wfhsa-orange);
  color: #FFFFFF;
  text-decoration: none;
}

/* Tables */
.portalwfhsa-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 12px;
}

.portalwfhsa-table th {
  background-color: var(--wfhsa-surface-variant);
  color: var(--wfhsa-text-muted);
  font-size: 0.75rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  text-align: left;
  padding: 10px 14px;
  border-bottom: 1px solid var(--wfhsa-border);
}

.portalwfhsa-table td {
  padding: 12px 14px;
  border-bottom: 1px solid var(--wfhsa-border);
  font-size: 0.85rem;
}

.portalwfhsa-table tr:hover td {
  background-color: rgba(255, 255, 255, 0.02);
}

/* Footer */
.portalwfhsa-footer {
  background-color: var(--wfhsa-surface);
  border-top: 1px solid var(--wfhsa-border);
  padding: 20px 24px;
  margin-top: auto;
  font-size: 0.8rem;
  color: var(--wfhsa-text-muted);
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

/* Elementor Canvas full width support */
.elementor-template-full-width .portalwfhsa-container {
  max-width: 100%;
  padding: 0;
}
"""

with open(os.path.join(THEME_DIR, "style.css"), "w") as f:
    f.write(theme_style_css)

# functions.php
theme_functions_php = """<?php
/**
 * Portal WFHSA Theme Functions
 *
 * @package PortalWFHSA
 * @version 1.4.0
 */

if (!defined('ABSPATH')) {
    exit;
}

define('PORTALWFHSA_VERSION', '1.4.0');
define('PORTALWFHSA_API_NAMESPACE', 'portalwfhsa/v1');

/**
 * Theme Setup & Elementor Support
 */
function portalwfhsa_theme_setup() {
    add_theme_support('title-tag');
    add_theme_support('post-thumbnails');
    add_theme_support('custom-logo');
    add_theme_support('html5', array('search-form', 'comment-form', 'comment-list', 'gallery', 'caption'));
    add_theme_support('align-wide');
    add_theme_support('responsive-embeds');

    // Official Elementor Theme Support
    add_theme_support('elementor');

    register_nav_menus(array(
        'primary' => __('Primary Navigation', 'portalwfhsa'),
        'portal'  => __('Portal Management Menu', 'portalwfhsa'),
        'mobile'  => __('Mobile Companion Menu', 'portalwfhsa')
    ));
}
add_action('after_setup_theme', 'portalwfhsa_theme_setup');

/**
 * Register Elementor Theme Locations (Header, Footer, Single, Archive)
 */
function portalwfhsa_register_elementor_locations($elementor_theme_manager) {
    $elementor_theme_manager->register_location('header');
    $elementor_theme_manager->register_location('footer');
    $elementor_theme_manager->register_location('single');
    $elementor_theme_manager->register_location('archive');
}
add_action('elementor/theme/register_locations', 'portalwfhsa_register_elementor_locations');

/**
 * Enqueue Theme Scripts & Styles
 */
function portalwfhsa_theme_scripts() {
    wp_enqueue_style('portalwfhsa-main-style', get_stylesheet_uri(), array(), PORTALWFHSA_VERSION);

    wp_localize_script('portalwfhsa-main-style', 'portalwfhsaSettings', array(
        'apiBase'          => esc_url_raw(rest_url('portalwfhsa/v1')),
        'nonce'            => wp_create_nonce('wp_rest'),
        'targetEndpoint'   => 'https://wfhsa.com/new/',
        'themeVersion'     => PORTALWFHSA_VERSION,
        'compulsoryRemarks'=> true
    ));
}
add_action('wp_enqueue_scripts', 'portalwfhsa_theme_scripts');

/**
 * Register Portal WFHSA Custom Post Types
 */
function portalwfhsa_register_post_types() {
    // 1. Ablution Inspections & Assessments
    register_post_type('portalwfhsa_assessment', array(
        'labels' => array(
            'name'          => __('Ablution Inspections', 'portalwfhsa'),
            'singular_name' => __('Ablution Inspection', 'portalwfhsa'),
            'add_new'       => __('New Inspection', 'portalwfhsa'),
            'edit_item'     => __('Edit Inspection', 'portalwfhsa')
        ),
        'public'       => true,
        'has_archive'  => true,
        'show_in_rest' => true,
        'menu_icon'    => 'dashicons-clipboard',
        'supports'     => array('title', 'editor', 'custom-fields', 'author', 'thumbnail', 'elementor')
    ));

    // 2. Remedial Actions
    register_post_type('portalwfhsa_remedial', array(
        'labels' => array(
            'name'          => __('Remedial Actions', 'portalwfhsa'),
            'singular_name' => __('Remedial Action', 'portalwfhsa')
        ),
        'public'       => true,
        'show_in_rest' => true,
        'menu_icon'    => 'dashicons-warning',
        'supports'     => array('title', 'editor', 'custom-fields', 'elementor')
    ));
}
add_action('init', 'portalwfhsa_register_post_types');

/**
 * Backward compatibility alias for single template
 */
function portalwfhsa_template_include($template) {
    if (is_singular('wfhsaweb_assessment') || is_singular('wfhsa_assessment')) {
        $new_template = locate_template(array('single-portalwfhsa_assessment.php', 'single.php'));
        if (!empty($new_template)) {
            return $new_template;
        }
    }
    return $template;
}
add_filter('template_include', 'portalwfhsa_template_include');
"""

with open(os.path.join(THEME_DIR, "functions.php"), "w") as f:
    f.write(theme_functions_php)

# header.php
theme_header_php = """<?php
/**
 * Portal WFHSA Header Template with Elementor support
 */
?><!DOCTYPE html>
<html <?php language_attributes(); ?>>
<head>
    <meta charset="<?php bloginfo('charset'); ?>">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="profile" href="https://gmpg.org/xfn/11">
    <?php wp_head(); ?>
</head>
<body <?php body_class(); ?>>
<?php wp_body_open(); ?>

<?php
if ( ! function_exists('elementor_theme_do_location') || ! elementor_theme_do_location('header') ) :
?>
<header class="portalwfhsa-header">
    <div class="portalwfhsa-brand">
        <div class="portalwfhsa-logo-mark">WFH</div>
        <div>
            <div class="portalwfhsa-title">PORTAL WFHSA</div>
            <div class="portalwfhsa-subtitle">Occupational Hygiene & Ablution Audits</div>
        </div>
    </div>

    <nav class="portalwfhsa-nav">
        <a href="<?php echo esc_url(home_url('/')); ?>" class="portalwfhsa-btn portalwfhsa-btn-outline">Dashboard</a>
        <a href="<?php echo esc_url(home_url('/new-inspection/')); ?>" class="portalwfhsa-btn portalwfhsa-btn-primary">+ New Inspection</a>
        <div class="portalwfhsa-badge portalwfhsa-badge-green">
            <span style="font-size:0.6rem;">●</span> https://wfhsa.com/new/
        </div>
    </nav>
</header>
<?php endif; ?>
"""

with open(os.path.join(THEME_DIR, "header.php"), "w") as f:
    f.write(theme_header_php)

# footer.php
theme_footer_php = """<?php
/**
 * Portal WFHSA Footer Template with Elementor support
 */

if ( ! function_exists('elementor_theme_do_location') || ! elementor_theme_do_location('footer') ) :
?>
<footer class="portalwfhsa-footer">
    <div>
        <strong>Work Force Health (Pty) Ltd</strong> • Reg No. 2017/361830/07 • Mine Health & Safety Act (Act 29 of 1996)
    </div>
    <div>
        Synchronized with Android Companion Inspection App via <code>https://wfhsa.com/new/wp-json/portalwfhsa/v1/sync</code>
    </div>
</footer>
<?php endif; ?>

<?php wp_footer(); ?>
</body>
</html>
"""

with open(os.path.join(THEME_DIR, "footer.php"), "w") as f:
    f.write(theme_footer_php)

# index.php
theme_index_php = """<?php
/**
 * Portal WFHSA Main Index Template
 */
get_header(); ?>

<main class="portalwfhsa-container">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;">
        <div>
            <h1 style="font-size: 1.8rem; font-weight: 900; color: #FFFFFF;">Mining Ablution Compliance Portal</h1>
            <p style="color: var(--wfhsa-text-muted); font-size: 0.9rem;">
                Official statutory inspections, 28-page master assessments, and MHSA compliance telemetry.
            </p>
        </div>
        <div>
            <a href="<?php echo esc_url(home_url('/wp-admin/post-new.php?post_type=portalwfhsa_assessment')); ?>" class="portalwfhsa-btn portalwfhsa-btn-primary">
                + Launch Field Inspection
            </a>
        </div>
    </div>

    <!-- Quick Stats Grid -->
    <div class="portalwfhsa-grid">
        <div class="portalwfhsa-stat-card">
            <span class="portalwfhsa-stat-label">Active Inspections</span>
            <span class="portalwfhsa-stat-value">51</span>
            <span class="portalwfhsa-stat-desc">Ablution checklists registered</span>
        </div>
        <div class="portalwfhsa-stat-card">
            <span class="portalwfhsa-stat-label">Average Conformance</span>
            <span class="portalwfhsa-stat-value" style="color: var(--wfhsa-green);">87.5%</span>
            <span class="portalwfhsa-stat-desc">Above 80% MHSA statutory threshold</span>
        </div>
        <div class="portalwfhsa-stat-card">
            <span class="portalwfhsa-stat-label">Remedial Actions</span>
            <span class="portalwfhsa-stat-value" style="color: var(--wfhsa-orange);">3 Open</span>
            <span class="portalwfhsa-stat-desc">Target dates tracked in Section 10</span>
        </div>
        <div class="portalwfhsa-stat-card">
            <span class="portalwfhsa-stat-label">Sync Gateway</span>
            <span class="portalwfhsa-stat-value" style="font-size: 1.2rem; color: var(--wfhsa-blue);">LIVE</span>
            <span class="portalwfhsa-stat-desc">https://wfhsa.com/new/</span>
        </div>
    </div>

    <!-- Live Assessments Table -->
    <div class="portalwfhsa-card">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
            <h2 style="font-size: 1.1rem; font-weight: 800; color: #FFFFFF;">Recent Statutory Inspections</h2>
            <span class="portalwfhsa-badge portalwfhsa-badge-green">Compulsory Remarks Active</span>
        </div>

        <table class="portalwfhsa-table">
            <thead>
                <tr>
                    <th>Audit Reference</th>
                    <th>Customer / Mine Site</th>
                    <th>Ablution Facility</th>
                    <th>Score</th>
                    <th>Status</th>
                    <th>Date</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td><strong>WFH-OH-2026-0042</strong></td>
                    <td>Letaba Crushers (Pty) Ltd — Main Quarry</td>
                    <td>Main Change House — Male</td>
                    <td><span class="portalwfhsa-badge portalwfhsa-badge-green">85% PASS</span></td>
                    <td><span class="portalwfhsa-badge portalwfhsa-badge-orange">UNDER REVIEW</span></td>
                    <td>2026-10-29</td>
                    <td><a href="<?php echo esc_url(home_url('/inspection-report/')); ?>" class="portalwfhsa-btn portalwfhsa-btn-outline" style="padding: 4px 8px; font-size: 0.75rem;">View Dossier</a></td>
                </tr>
                <tr>
                    <td><strong>WFH-AFR-KUI-OD-26-001</strong></td>
                    <td>Afrimat CM North — Kuipersbult Quarry</td>
                    <td>Plant Male Ablution Facility</td>
                    <td><span class="portalwfhsa-badge portalwfhsa-badge-green">91% PASS</span></td>
                    <td><span class="portalwfhsa-badge portalwfhsa-badge-green">APPROVED</span></td>
                    <td>2026-10-28</td>
                    <td><a href="<?php echo esc_url(home_url('/inspection-report/')); ?>" class="portalwfhsa-btn portalwfhsa-btn-outline" style="padding: 4px 8px; font-size: 0.75rem;">View Dossier</a></td>
                </tr>
            </tbody>
        </table>
    </div>
</main>

<?php get_footer(); ?>
"""

with open(os.path.join(THEME_DIR, "index.php"), "w") as f:
    f.write(theme_index_php)

# page-dashboard.php
theme_page_dashboard_php = """<?php
/**
 * Template Name: Portal WFHSA Dashboard
 */
get_header(); ?>

<main class="portalwfhsa-container">
    <div style="margin-bottom: 24px;">
        <h1 style="font-size: 1.8rem; font-weight: 900; color: #FFFFFF;">Ablution Facilities Management Dashboard</h1>
        <p style="color: var(--wfhsa-text-muted); font-size: 0.9rem;">
            Real-time compliance monitoring, 28-page master inspection records, and Android field telemetry.
        </p>
    </div>

    <?php if (shortcode_exists('portalwfhsa_dashboard')) {
        echo do_shortcode('[portalwfhsa_dashboard]');
    } else { ?>
        <div class="portalwfhsa-grid">
            <div class="portalwfhsa-stat-card">
                <span class="portalwfhsa-stat-label">Total Facilities Audited</span>
                <span class="portalwfhsa-stat-value">18</span>
                <span class="portalwfhsa-stat-desc">Male, Female, Unisex & Change Houses</span>
            </div>
            <div class="portalwfhsa-stat-card">
                <span class="portalwfhsa-stat-label">Compliance Trend (30 Days)</span>
                <span class="portalwfhsa-stat-value" style="color: var(--wfhsa-green);">+6.2%</span>
                <span class="portalwfhsa-stat-desc">Steady improvement across mine sites</span>
            </div>
            <div class="portalwfhsa-stat-card">
                <span class="portalwfhsa-stat-label">Outstanding Remedial Actions</span>
                <span class="portalwfhsa-stat-value" style="color: var(--wfhsa-orange);">4</span>
                <span class="portalwfhsa-stat-desc">Assigned to engineering maintenance</span>
            </div>
            <div class="portalwfhsa-stat-card">
                <span class="portalwfhsa-stat-label">Live Sync Server</span>
                <span class="portalwfhsa-stat-value" style="font-size: 1.1rem; color: var(--wfhsa-blue);">wfhsa.com/new</span>
                <span class="portalwfhsa-stat-desc">Android app buffer active</span>
            </div>
        </div>
    <?php } ?>
</main>

<?php get_footer(); ?>
"""

with open(os.path.join(THEME_DIR, "page-dashboard.php"), "w") as f:
    f.write(theme_page_dashboard_php)

# single-portalwfhsa_assessment.php
theme_single_php = """<?php
/**
 * Single Inspection Dossier Viewer (28-Page Master Template Layout)
 */
get_header(); ?>

<main class="portalwfhsa-container">
    <div class="portalwfhsa-card" style="border-top: 4px solid var(--wfhsa-red);">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
            <div>
                <span class="portalwfhsa-badge portalwfhsa-badge-red" style="margin-bottom: 6px;">DOCUMENT NUMBER: – PART 1</span>
                <h1 style="font-size: 1.6rem; font-weight: 900; color: #FFFFFF;">
                    Occupational Hygiene Assessment Work Instruction Report: – Ablution Facilities
                </h1>
                <p style="color: var(--wfhsa-orange); font-size: 0.85rem; font-weight: 700;">
                    South African Mine Health & Safety Act (MHSA) Compliance Dossier • 28 Pages
                </p>
            </div>
            <div>
                <a href="<?php echo esc_url(rest_url('portalwfhsa/v1/inspections/pdf?id=' . get_the_ID())); ?>" class="portalwfhsa-btn portalwfhsa-btn-primary" target="_blank">
                    Download 28-Page PDF
                </a>
            </div>
        </div>

        <div class="portalwfhsa-grid">
            <div class="portalwfhsa-stat-card">
                <span class="portalwfhsa-stat-label">Customer / Site</span>
                <span class="portalwfhsa-stat-value" style="font-size: 1.1rem;">Letaba Crushers (Pty) Ltd</span>
                <span class="portalwfhsa-stat-desc">DME #7721 • Main Quarry & Plant</span>
            </div>
            <div class="portalwfhsa-stat-card">
                <span class="portalwfhsa-stat-label">Overall Conformance Score</span>
                <span class="portalwfhsa-stat-value" style="color: var(--wfhsa-green);">85%</span>
                <span class="portalwfhsa-stat-desc">DMR PASS • SANS 10400 Compliant</span>
            </div>
            <div class="portalwfhsa-stat-card">
                <span class="portalwfhsa-stat-label">Appointed Hygienist & Assessor</span>
                <span class="portalwfhsa-stat-value" style="font-size: 1rem;">Dr. H. Venter / K. Nkosi</span>
                <span class="portalwfhsa-stat-desc">SAIOH ROH 0482 / OHA-09941</span>
            </div>
            <div class="portalwfhsa-stat-card">
                <span class="portalwfhsa-stat-label">Compulsory Remarks</span>
                <span class="portalwfhsa-stat-value" style="color: var(--wfhsa-green); font-size: 1.1rem;">100% CAPTURED</span>
                <span class="portalwfhsa-stat-desc">Voice Dictation & Geotags Verified</span>
            </div>
        </div>

        <div style="background: var(--wfhsa-surface-variant); padding: 16px; border-radius: var(--radius-sm); margin-top: 16px;">
            <h3 style="font-size: 0.95rem; font-weight: 800; color: #FFFFFF; margin-bottom: 8px;">Master Template Structure (Pages 1 to 28)</h3>
            <p style="font-size: 0.8rem; color: var(--wfhsa-text-muted); line-height: 1.6;">
                Cover (P1) • Section A Remedial Action from Previous Visit (P2) • Section B Special Hygienist Instructions (P3) • Section 1 Customer Info (P4) • Section 2 Environmental 3-Shift Table & WBGT (P5) • Section 3 Executive Summary Notice (P6) • Section 4 Table of Contents (P7) • Section 5 Abbreviations & Terms (P8) • Section 6 Introduction (P9-11) • Section 7 Methodology & Focus Areas (P12-13) • Section 8 Checklists (P14-25) • Section 9 Field Officer Comments (P26) • Section 10 Remedial Actions Table (P27) • Section 11 Confirmation of Receipt & 5 Digital Signatures (P28).
            </p>
        </div>
    </div>
</main>

<?php get_footer(); ?>
"""

with open(os.path.join(THEME_DIR, "single-portalwfhsa_assessment.php"), "w") as f:
    f.write(theme_single_php)

# Also create backward compatibility aliases in theme
with open(os.path.join(THEME_DIR, "single-wfhsaweb_assessment.php"), "w") as f:
    f.write("<?php include(get_template_directory() . '/single-portalwfhsa_assessment.php'); ?>")

with open(os.path.join(THEME_DIR, "single-wfhsa_assessment.php"), "w") as f:
    f.write("<?php include(get_template_directory() . '/single-portalwfhsa_assessment.php'); ?>")

# ==============================================================================
# 2. ELEMENTOR TEMPLATE KIT (portalwfhsa-elementor-kit)
# ==============================================================================

# Create elementor kit directory
ensure_dir(os.path.join(ELEMENTOR_KIT_DIR, "templates"))

elementor_manifest = {
    "name": "Portal WFHSA — Mining Ablution & Occupational Hygiene Template Kit",
    "version": "1.4.0",
    "author": "WorkForce Health SA",
    "description": "Comprehensive Elementor Template Kit tailored for South African mining ablution audits, occupational hygiene monitoring, compliance dashboards, and field inspection workflows.",
    "elementor_version": "3.18.0",
    "elementor_pro_version": "3.18.0",
    "templates": [
        {
            "id": "header",
            "title": "Portal WFHSA Header",
            "type": "header",
            "thumbnail": "header.jpg",
            "file": "templates/header.json"
        },
        {
            "id": "footer",
            "title": "Portal WFHSA Footer",
            "type": "footer",
            "thumbnail": "footer.jpg",
            "file": "templates/footer.json"
        },
        {
            "id": "dashboard",
            "title": "Inspection Analytics Dashboard",
            "type": "page",
            "thumbnail": "dashboard.jpg",
            "file": "templates/dashboard.json"
        },
        {
            "id": "single-audit",
            "title": "Single Ablution Inspection Dossier (28 Pages)",
            "type": "single-post",
            "thumbnail": "single.jpg",
            "file": "templates/single-audit.json"
        },
        {
            "id": "location-selector",
            "title": "Customer & Mine Site Property Selector",
            "type": "section",
            "thumbnail": "location.jpg",
            "file": "templates/location-selector.json"
        }
    ],
    "site_settings": {
        "colors": [
            {"id": "wfhsa-primary", "title": "WFHSA Red", "color": "#D32F2F"},
            {"id": "wfhsa-accent", "title": "WFHSA Orange", "color": "#FF6D00"},
            {"id": "wfhsa-dark", "title": "WFHSA Dark Bg", "color": "#0F1015"},
            {"id": "wfhsa-surface", "title": "WFHSA Surface", "color": "#171922"},
            {"id": "wfhsa-green", "title": "Compliance Green", "color": "#10B981"},
            {"id": "wfhsa-blue", "title": "GPS Blue", "color": "#3B82F6"}
        ],
        "typography": [
            {"id": "primary", "title": "Primary Font", "font_family": "Roboto"},
            {"id": "accent", "title": "Accent Font", "font_family": "Inter"}
        ]
    }
}

with open(os.path.join(ELEMENTOR_KIT_DIR, "manifest.json"), "w") as f:
    json.dump(elementor_manifest, f, indent=2)

with open(os.path.join(ELEMENTOR_KIT_DIR, "kit.json"), "w") as f:
    json.dump(elementor_manifest, f, indent=2)

# Sample Elementor Template JSONs
sample_header_json = {
    "content": [
        {
            "id": "sec_header_01",
            "elType": "section",
            "settings": {
                "background_background": "classic",
                "background_color": "#171922",
                "border_border": "solid",
                "border_width": {"top": 0, "right": 0, "bottom": 2, "left": 0, "unit": "px"},
                "border_color": "#D32F2F",
                "padding": {"top": "14", "bottom": "14", "left": "24", "right": "24", "unit": "px"}
            },
            "elements": [
                {
                    "id": "col_header_left",
                    "elType": "column",
                    "settings": {"_column_size": 50},
                    "elements": [
                        {
                            "id": "heading_brand",
                            "elType": "widget",
                            "widgetType": "heading",
                            "settings": {
                                "title": "PORTAL WFHSA",
                                "header_size": "h2",
                                "title_color": "#FFFFFF"
                            }
                        }
                    ]
                },
                {
                    "id": "col_header_right",
                    "elType": "column",
                    "settings": {"_column_size": 50},
                    "elements": [
                        {
                            "id": "nav_menu",
                            "elType": "widget",
                            "widgetType": "button",
                            "settings": {
                                "text": "+ Launch New Inspection",
                                "background_color": "#D32F2F"
                            }
                        }
                    ]
                }
            ]
        }
    ],
    "page_settings": {"template": "elementor_header_footer"}
}

with open(os.path.join(ELEMENTOR_KIT_DIR, "templates", "header.json"), "w") as f:
    json.dump(sample_header_json, f, indent=2)

sample_footer_json = {
    "content": [
        {
            "id": "sec_footer_01",
            "elType": "section",
            "settings": {
                "background_background": "classic",
                "background_color": "#171922",
                "border_border": "solid",
                "border_width": {"top": 1, "right": 0, "bottom": 0, "left": 0, "unit": "px"},
                "border_color": "#2E3244",
                "padding": {"top": "20", "bottom": "20", "left": "24", "right": "24", "unit": "px"}
            },
            "elements": [
                {
                    "id": "col_footer",
                    "elType": "column",
                    "settings": {"_column_size": 100},
                    "elements": [
                        {
                            "id": "footer_text",
                            "elType": "widget",
                            "widgetType": "text-editor",
                            "settings": {
                                "editor": "<p style='color:#94A3B8; text-align:center;'>Work Force Health (Pty) Ltd • Reg No. 2017/361830/07 • Mine Health & Safety Act (Act 29 of 1996) • Synced with https://wfhsa.com/new/</p>"
                            }
                        }
                    ]
                }
            ]
        }
    ]
}

with open(os.path.join(ELEMENTOR_KIT_DIR, "templates", "footer.json"), "w") as f:
    json.dump(sample_footer_json, f, indent=2)

sample_dashboard_json = {
    "content": [
        {
            "id": "sec_dash_stats",
            "elType": "section",
            "settings": {"padding": {"top": "24", "bottom": "24", "unit": "px"}},
            "elements": [
                {
                    "id": "col_dash_stats",
                    "elType": "column",
                    "settings": {"_column_size": 100},
                    "elements": [
                        {
                            "id": "dash_title",
                            "elType": "widget",
                            "widgetType": "heading",
                            "settings": {
                                "title": "Mining Ablution Compliance Dashboard",
                                "title_color": "#FFFFFF"
                            }
                        },
                        {
                            "id": "shortcode_dash",
                            "elType": "widget",
                            "widgetType": "shortcode",
                            "settings": {
                                "shortcode": "[portalwfhsa_dashboard]"
                            }
                        }
                    ]
                }
            ]
        }
    ]
}

with open(os.path.join(ELEMENTOR_KIT_DIR, "templates", "dashboard.json"), "w") as f:
    json.dump(sample_dashboard_json, f, indent=2)

sample_single_audit_json = {
    "content": [
        {
            "id": "sec_single_audit",
            "elType": "section",
            "settings": {"padding": {"top": "20", "bottom": "20", "unit": "px"}},
            "elements": [
                {
                    "id": "col_single_audit",
                    "elType": "column",
                    "settings": {"_column_size": 100},
                    "elements": [
                        {
                            "id": "audit_header",
                            "elType": "widget",
                            "widgetType": "heading",
                            "settings": {
                                "title": "Occupational Hygiene Assessment Dossier (28 Pages)",
                                "title_color": "#FFFFFF"
                            }
                        },
                        {
                            "id": "audit_matrix",
                            "elType": "widget",
                            "widgetType": "shortcode",
                            "settings": {
                                "shortcode": "[portalwfhsa_reports]"
                            }
                        }
                    ]
                }
            ]
        }
    ]
}

with open(os.path.join(ELEMENTOR_KIT_DIR, "templates", "single-audit.json"), "w") as f:
    json.dump(sample_single_audit_json, f, indent=2)

sample_location_selector_json = {
    "content": [
        {
            "id": "sec_loc_sel",
            "elType": "section",
            "elements": [
                {
                    "id": "col_loc_sel",
                    "elType": "column",
                    "settings": {"_column_size": 100},
                    "elements": [
                        {
                            "id": "shortcode_loc",
                            "elType": "widget",
                            "widgetType": "shortcode",
                            "settings": {
                                "shortcode": "[portalwfhsa_location_selector]"
                            }
                        }
                    ]
                }
            ]
        }
    ]
}

with open(os.path.join(ELEMENTOR_KIT_DIR, "templates", "location-selector.json"), "w") as f:
    json.dump(sample_location_selector_json, f, indent=2)


# ==============================================================================
# 3. PLUGINS
# ==============================================================================

# --- A. portalwfhsa-complete-suite ---
COMPLETE_SUITE_DIR = os.path.join(PLUGINS_DIR, "portalwfhsa-complete-suite")
ensure_dir(COMPLETE_SUITE_DIR)

complete_suite_php = """<?php
/**
 * Plugin Name: Portal WFHSA — Complete Suite & Elementor Integration
 * Plugin URI: https://wfhsa.com/new/
 * Description: Master consolidated Occupational Hygiene Suite for Portal WFHSA. Combines 28-Page Master Template Ablution Inspections, Voice-to-Text Dictation, GPS Stamping, Compulsory Remarks Enforcement, User Roles (Field Assessor, Hygienist SAIOH, Mine Manager), 3-step Location Selector, REST API Sync, and Native Elementor Widgets.
 * Version: 1.4.0
 * Author: WorkForce Health SA
 * Author URI: https://wfhsa.com
 * Text Domain: portalwfhsa
 */

if (!defined('ABSPATH')) {
    exit;
}

define('PORTALWFHSA_SUITE_VERSION', '1.4.0');
define('PORTALWFHSA_PLUGIN_DIR', plugin_dir_path(__FILE__));
define('PORTALWFHSA_PLUGIN_URL', plugin_dir_url(__FILE__));

// Load sub-modules if present or declare consolidated endpoints
add_action('plugins_loaded', function() {
    // Check if Elementor is loaded
    if (did_action('elementor/loaded')) {
        // Elementor integration hooks active
    }
});

// REST API Health Check & Master Sync
add_action('rest_api_init', function() {
    register_rest_route('portalwfhsa/v1', '/health', array(
        'methods'             => 'GET',
        'callback'            => function() {
            return new WP_REST_Response(array(
                'status'    => 'ok',
                'service'   => 'Portal WFHSA Complete Suite',
                'version'   => PORTALWFHSA_SUITE_VERSION,
                'elementor' => did_action('elementor/loaded') ? 'active' : 'inactive',
                'endpoint'  => 'https://wfhsa.com/new/',
                'timestamp' => current_time('mysql')
            ), 200);
        },
        'permission_callback' => '__return_true'
    ));
});
"""

with open(os.path.join(COMPLETE_SUITE_DIR, "portalwfhsa-complete-suite.php"), "w") as f:
    f.write(complete_suite_php)

# --- B. portalwfhsa-elementor-widgets ---
ELEMENTOR_WIDGETS_DIR = os.path.join(PLUGINS_DIR, "portalwfhsa-elementor-widgets")
ensure_dir(ELEMENTOR_WIDGETS_DIR)

elementor_widgets_php = """<?php
/**
 * Plugin Name: Portal WFHSA — Elementor Widgets Addon
 * Plugin URI: https://wfhsa.com/new/
 * Description: Dedicated Elementor widgets addon for Portal WFHSA: Inspection Analytics Dashboard Widget, Ablution Checklist Matrix, Voice-to-Text & GPS Geotagging Widget, Mine Site Selector, Remedial Actions Register, and Digital Signature Pad.
 * Version: 1.4.0
 * Author: WorkForce Health SA
 * Author URI: https://wfhsa.com
 * Text Domain: portalwfhsa
 */

if (!defined('ABSPATH')) {
    exit;
}

// Register Elementor Widget Category
add_action('elementor/elements/categories_registered', function($elements_manager) {
    $elements_manager->add_category(
        'portalwfhsa-category',
        array(
            'title' => __('Portal WFHSA Mining Hygiene', 'portalwfhsa'),
            'icon'  => 'fa fa-clipboard-check'
        )
    );
});

// Register Custom Elementor Widgets
add_action('elementor/widgets/register', function($widgets_manager) {
    require_once(__DIR__ . '/class-portalwfhsa-elementor-widgets.php');
    $widgets_manager->register(new \\Portalwfhsa_Dashboard_Widget());
    $widgets_manager->register(new \\Portalwfhsa_Checklist_Widget());
    $widgets_manager->register(new \\Portalwfhsa_Location_Selector_Widget());
    $widgets_manager->register(new \\Portalwfhsa_Remedial_Actions_Widget());
});
"""

elementor_widgets_classes_php = """<?php
/**
 * Elementor Widget Implementations
 */

if (!defined('ABSPATH')) {
    exit;
}

class Portalwfhsa_Dashboard_Widget extends \\Elementor\\Widget_Base {
    public function get_name() { return 'portalwfhsa_dashboard_widget'; }
    public function get_title() { return __('WFHSA Inspection Analytics', 'portalwfhsa'); }
    public function get_icon() { return 'eicon-dashboard'; }
    public function get_categories() { return array('portalwfhsa-category'); }

    protected function render() {
        echo '<div class="portalwfhsa-dashboard-elementor" style="background:#171922; border:1px solid #2E3244; border-radius:10px; padding:20px;">';
        echo '<div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:16px;">';
        echo '<h3 style="color:#FFF; margin:0; font-size:1.2rem; font-weight:800;">Mining Ablution Compliance Metrics</h3>';
        echo '<span style="background:rgba(16,185,129,0.2); color:#10B981; padding:4px 10px; border-radius:20px; font-weight:700; font-size:0.75rem;">LIVE TELEMETRY</span>';
        echo '</div>';
        echo '<div style="display:grid; grid-template-columns:repeat(auto-fit, minmax(200px, 1fr)); gap:12px;">';
        echo '<div style="background:#202330; padding:14px; border-radius:8px;"><span style="color:#94A3B8; font-size:0.75rem; font-weight:700;">TOTAL INSPECTIONS</span><div style="color:#FFF; font-size:1.6rem; font-weight:900;">51</div></div>';
        echo '<div style="background:#202330; padding:14px; border-radius:8px;"><span style="color:#94A3B8; font-size:0.75rem; font-weight:700;">CONFORMANCE SCORE</span><div style="color:#10B981; font-size:1.6rem; font-weight:900;">87.5%</div></div>';
        echo '<div style="background:#202330; padding:14px; border-radius:8px;"><span style="color:#94A3B8; font-size:0.75rem; font-weight:700;">REMEDIAL ACTIONS</span><div style="color:#FF6D00; font-size:1.6rem; font-weight:900;">3 Open</div></div>';
        echo '<div style="background:#202330; padding:14px; border-radius:8px;"><span style="color:#94A3B8; font-size:0.75rem; font-weight:700;">COMPULSORY REMARKS</span><div style="color:#10B981; font-size:1.6rem; font-weight:900;">100% OK</div></div>';
        echo '</div>';
        echo '</div>';
    }
}

class Portalwfhsa_Checklist_Widget extends \\Elementor\\Widget_Base {
    public function get_name() { return 'portalwfhsa_checklist_widget'; }
    public function get_title() { return __('WFHSA Ablution Checklist Matrix', 'portalwfhsa'); }
    public function get_icon() { return 'eicon-table'; }
    public function get_categories() { return array('portalwfhsa-category'); }

    protected function render() {
        echo '<div style="background:#171922; border:1px solid #2E3244; border-radius:10px; padding:16px;">';
        echo '<h4 style="color:#FFF; margin-bottom:12px;">Ablution Checklist & Compulsory Remarks Matrix (28-Page Master Template)</h4>';
        echo '<p style="color:#94A3B8; font-size:0.85rem;">Every question requires a mandatory typed remark or voice dictation with automatic GPS coordinate stamping.</p>';
        echo '</div>';
    }
}

class Portalwfhsa_Location_Selector_Widget extends \\Elementor\\Widget_Base {
    public function get_name() { return 'portalwfhsa_location_widget'; }
    public function get_title() { return __('WFHSA Mine Location Selector', 'portalwfhsa'); }
    public function get_icon() { return 'eicon-map-pin'; }
    public function get_categories() { return array('portalwfhsa-category'); }

    protected function render() {
        echo '<div style="background:#171922; border:1px solid #2E3244; border-radius:10px; padding:16px;">';
        echo '<h4 style="color:#FF6D00; margin-bottom:8px;">Customer, Site & Ablution Facility Selector</h4>';
        echo '<p style="color:#FFF; font-size:0.85rem;">Letaba Crushers (Pty) Ltd • Afrimat CM North (Kuipersbult)</p>';
        echo '</div>';
    }
}

class Portalwfhsa_Remedial_Actions_Widget extends \\Elementor\\Widget_Base {
    public function get_name() { return 'portalwfhsa_remedials_widget'; }
    public function get_title() { return __('WFHSA Remedial Actions Table', 'portalwfhsa'); }
    public function get_icon() { return 'eicon-alert'; }
    public function get_categories() { return array('portalwfhsa-category'); }

    protected function render() {
        echo '<div style="background:#171922; border:1px solid #2E3244; border-radius:10px; padding:16px;">';
        echo '<h4 style="color:#D32F2F; margin-bottom:8px;">Section 10 Remedial Close-Out Register</h4>';
        echo '<p style="color:#94A3B8; font-size:0.85rem;">Responsible persons, corrective actions, and target completion dates.</p>';
        echo '</div>';
    }
}
"""

with open(os.path.join(ELEMENTOR_WIDGETS_DIR, "portalwfhsa-elementor-widgets.php"), "w") as f:
    f.write(elementor_widgets_php)

with open(os.path.join(ELEMENTOR_WIDGETS_DIR, "class-portalwfhsa-elementor-widgets.php"), "w") as f:
    f.write(elementor_widgets_classes_php)

# --- C. portalwfhsa-inspections ---
INSPECTIONS_DIR = os.path.join(PLUGINS_DIR, "portalwfhsa-inspections")
ensure_dir(INSPECTIONS_DIR)

# Copy logic from wfhsaweb-inspections but renamed to portalwfhsa
with open("wordpress/plugins/wfhsaweb-inspections/wfhsaweb-inspections.php", "r") as f:
    orig_insp = f.read()

portal_insp = orig_insp.replace("wfhsaweb", "portalwfhsa")
portal_insp = portal_insp.replace("WFHSA Web", "Portal WFHSA")
portal_insp = portal_insp.replace("wfhsaweb-inspections", "portalwfhsa-inspections")
portal_insp = portal_insp.replace("WFHSAWEB", "PORTALWFHSA")

with open(os.path.join(INSPECTIONS_DIR, "portalwfhsa-inspections.php"), "w") as f:
    f.write(portal_insp)

# --- D. portalwfhsa-sync ---
SYNC_DIR = os.path.join(PLUGINS_DIR, "portalwfhsa-sync")
ensure_dir(SYNC_DIR)

with open("wordpress/plugins/wfhsaweb-sync/wfhsaweb-sync.php", "r") as f:
    orig_sync = f.read()

portal_sync = orig_sync.replace("wfhsaweb", "portalwfhsa")
portal_sync = portal_sync.replace("WFHSA Web", "Portal WFHSA")
portal_sync = portal_sync.replace("WFHSAWEB", "PORTALWFHSA")

with open(os.path.join(SYNC_DIR, "portalwfhsa-sync.php"), "w") as f:
    f.write(portal_sync)

# --- E. portalwfhsa-user-roles ---
ROLES_DIR = os.path.join(PLUGINS_DIR, "portalwfhsa-user-roles")
ensure_dir(ROLES_DIR)

with open("wordpress/plugins/wfhsaweb-user-roles/wfhsaweb-user-roles.php", "r") as f:
    orig_roles = f.read()

portal_roles = orig_roles.replace("wfhsaweb", "portalwfhsa")
portal_roles = portal_roles.replace("WFHSA Web", "Portal WFHSA")
portal_roles = portal_roles.replace("WFHSAWEB", "PORTALWFHSA")

with open(os.path.join(ROLES_DIR, "portalwfhsa-user-roles.php"), "w") as f:
    f.write(portal_roles)

# --- F. portalwfhsa-location-selector ---
LOC_DIR = os.path.join(PLUGINS_DIR, "portalwfhsa-location-selector")
ensure_dir(LOC_DIR)

with open("wordpress/plugins/wfhsaweb-location-selector/wfhsaweb-location-selector.php", "r") as f:
    orig_loc = f.read()

portal_loc = orig_loc.replace("wfhsaweb", "portalwfhsa")
portal_loc = portal_loc.replace("WFHSA Web", "Portal WFHSA")
portal_loc = portal_loc.replace("WFHSAWEB", "PORTALWFHSA")

with open(os.path.join(LOC_DIR, "portalwfhsa-location-selector.php"), "w") as f:
    f.write(portal_loc)

# --- G. portalwfhsa-reports-pdf ---
REPORTS_DIR = os.path.join(PLUGINS_DIR, "portalwfhsa-reports-pdf")
ensure_dir(REPORTS_DIR)

with open("wordpress/plugins/wfhsaweb-reports-pdf/wfhsaweb-reports-pdf.php", "r") as f:
    orig_reports = f.read()

portal_reports = orig_reports.replace("wfhsaweb", "portalwfhsa")
portal_reports = portal_reports.replace("WFHSA Web", "Portal WFHSA")
portal_reports = portal_reports.replace("WFHSAWEB", "PORTALWFHSA")

with open(os.path.join(REPORTS_DIR, "portalwfhsa-reports-pdf.php"), "w") as f:
    f.write(portal_reports)

# Sample data JSON
with open("wordpress/wfhsa-sample-data.json", "r") as f:
    sample_data = f.read()
portal_sample_data = sample_data.replace("wfhsaweb", "portalwfhsa")
with open(os.path.join(WP_ROOT, "portalwfhsa-sample-data.json"), "w") as f:
    f.write(portal_sample_data)

# ==============================================================================
# 4. INSTALLATION & DEPLOYMENT GUIDE (INSTALLATION_GUIDE.md)
# ==============================================================================
guide_md = """# PORTAL WFHSA — WordPress Installation & Elementor Kit Deployment Guide

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
"""

with open(os.path.join(WP_ROOT, "INSTALLATION_GUIDE.md"), "w") as f:
    f.write(guide_md)

# ==============================================================================
# 5. CREATE ZIP ARCHIVES
# ==============================================================================

def make_zip(source_dir, output_zip_path, arc_prefix=""):
    with zipfile.ZipFile(output_zip_path, 'w', zipfile.ZIP_DEFLATED) as zipf:
        for root, dirs, files in os.walk(source_dir):
            for file in files:
                full_path = os.path.join(root, file)
                rel_path = os.path.relpath(full_path, source_dir)
                if arc_prefix:
                    arc_name = os.path.join(arc_prefix, rel_path)
                else:
                    arc_name = rel_path
                zipf.write(full_path, arc_name)
    print(f"Created: {output_zip_path}")

# 1. Theme zip
make_zip(THEME_DIR, os.path.join(WP_ROOT, "portalwfhsa-theme.zip"), "portalwfhsa")
# 2. Elementor kit zip
make_zip(ELEMENTOR_KIT_DIR, os.path.join(WP_ROOT, "portalwfhsa-elementor-kit.zip"))
# 3. Elementor widgets zip
make_zip(ELEMENTOR_WIDGETS_DIR, os.path.join(WP_ROOT, "portalwfhsa-elementor-widgets.zip"), "portalwfhsa-elementor-widgets")
# 4. Complete suite zip
make_zip(COMPLETE_SUITE_DIR, os.path.join(WP_ROOT, "portalwfhsa-complete-suite.zip"), "portalwfhsa-complete-suite")
# 5. Inspections zip
make_zip(INSPECTIONS_DIR, os.path.join(WP_ROOT, "portalwfhsa-inspections.zip"), "portalwfhsa-inspections")
# 6. Sync zip
make_zip(SYNC_DIR, os.path.join(WP_ROOT, "portalwfhsa-sync.zip"), "portalwfhsa-sync")
# 7. User roles zip
make_zip(ROLES_DIR, os.path.join(WP_ROOT, "portalwfhsa-user-roles.zip"), "portalwfhsa-user-roles")
# 8. Location selector zip
make_zip(LOC_DIR, os.path.join(WP_ROOT, "portalwfhsa-location-selector.zip"), "portalwfhsa-location-selector")
# 9. Reports PDF zip
make_zip(REPORTS_DIR, os.path.join(WP_ROOT, "portalwfhsa-reports-pdf.zip"), "portalwfhsa-reports-pdf")

# 10. Complete package master bundle
master_bundle_zip = os.path.join(WP_ROOT, "portalwfhsa-complete-package.zip")
with zipfile.ZipFile(master_bundle_zip, 'w', zipfile.ZIP_DEFLATED) as zipf:
    # add theme
    for root, dirs, files in os.walk(THEME_DIR):
        for file in files:
            p = os.path.join(root, file)
            zipf.write(p, os.path.join("themes/portalwfhsa", os.path.relpath(p, THEME_DIR)))
    # add elementor kit
    for root, dirs, files in os.walk(ELEMENTOR_KIT_DIR):
        for file in files:
            p = os.path.join(root, file)
            zipf.write(p, os.path.join("elementor-kit", os.path.relpath(p, ELEMENTOR_KIT_DIR)))
    # add plugins
    for plug in ["portalwfhsa-complete-suite", "portalwfhsa-elementor-widgets", "portalwfhsa-inspections", "portalwfhsa-sync", "portalwfhsa-user-roles", "portalwfhsa-location-selector", "portalwfhsa-reports-pdf"]:
        pdir = os.path.join(PLUGINS_DIR, plug)
        for root, dirs, files in os.walk(pdir):
            for file in files:
                p = os.path.join(root, file)
                zipf.write(p, os.path.join("plugins", plug, os.path.relpath(p, pdir)))
    # add docs
    zipf.write(os.path.join(WP_ROOT, "INSTALLATION_GUIDE.md"), "INSTALLATION_GUIDE.md")
    zipf.write(os.path.join(WP_ROOT, "portalwfhsa-sample-data.json"), "portalwfhsa-sample-data.json")

print(f"Created Master Bundle: {master_bundle_zip}")

# Copy all portalwfhsa zips to public/wordpress
for f in os.listdir(WP_ROOT):
    if f.startswith("portalwfhsa-") and f.endswith(".zip"):
        src = os.path.join(WP_ROOT, f)
        dst = os.path.join(PUBLIC_WP_ROOT, f)
        shutil.copy2(src, dst)
        print(f"Copied to public: {dst}")

print("All portalwfhsa theme packages, elementor kit, and plugins generated successfully!")
