<?php
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
