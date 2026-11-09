<?php
/**
 * WORKFORCE HEALTH SA — Theme Functions & Definitions
 * URI: https://wfhsa.com
 */

if (!defined('ABSPATH')) {
    exit;
}

function wfhsa_theme_setup() {
    add_theme_support('title-tag');
    add_theme_support('post-thumbnails');
    add_theme_support('custom-logo', array(
        'height'      => 80,
        'width'       => 240,
        'flex-height' => true,
        'flex-width'  => true,
    ));

    register_nav_menus(array(
        'primary' => __('Primary Navigation', 'wfhsa-theme'),
        'portal'  => __('Management Portal Menu', 'wfhsa-theme'),
    ));
}
add_action('after_setup_theme', 'wfhsa_theme_setup');

function wfhsa_theme_scripts() {
    wp_enqueue_style('wfhsa-main-style', get_stylesheet_uri(), array(), '1.0.0');
}
add_action('wp_enqueue_scripts', 'wfhsa_theme_scripts');

/**
 * Expose WorkForce Health SA API Configuration to frontend
 */
function wfhsa_localize_api_settings() {
    wp_localize_script('wfhsa-main-style', 'wfhsaSettings', array(
        'apiBase' => esc_url_raw(rest_url('wfhsa/v1')),
        'nonce'   => wp_create_nonce('wp_rest'),
        'siteUrl' => esc_url(home_url('/')),
    ));
}
add_action('wp_enqueue_scripts', 'wfhsa_localize_api_settings');
