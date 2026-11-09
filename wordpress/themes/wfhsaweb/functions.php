<?php
/**
 * WFHSA Web — Theme Functions & Definitions
 * URI: https://wfhsa.com/new/
 * Package: WFHSA Web
 */

if (!defined('ABSPATH')) {
    exit;
}

define('WFHSAWEB_THEME_VERSION', '1.3.0');

/**
 * Theme Setup
 */
function wfhsaweb_theme_setup() {
    add_theme_support('title-tag');
    add_theme_support('post-thumbnails');
    add_theme_support('html5', array('search-form', 'comment-form', 'comment-list', 'gallery', 'caption'));
    add_theme_support('custom-logo', array(
        'height'      => 80,
        'width'       => 260,
        'flex-height' => true,
        'flex-width'  => true,
    ));

    register_nav_menus(array(
        'primary' => __('Primary Navigation', 'wfhsaweb'),
        'portal'  => __('Management Portal Menu', 'wfhsaweb'),
        'mobile'  => __('Mobile Quick Menu', 'wfhsaweb'),
    ));
}
add_action('after_setup_theme', 'wfhsaweb_theme_setup');

/**
 * Enqueue Styles and Scripts
 */
function wfhsaweb_theme_scripts() {
    wp_enqueue_style('wfhsaweb-main-style', get_stylesheet_uri(), array(), WFHSAWEB_THEME_VERSION);
    wp_enqueue_style('dashicons');

    // Localize API settings for companion app & front-end widgets
    wp_localize_script('wfhsaweb-main-style', 'wfhsawebSettings', array(
        'apiBase'          => esc_url_raw(rest_url('wfhsaweb/v1')),
        'mobiApiBase'      => esc_url_raw(rest_url('mobiworkforce/v1')),
        'legacyApiBase'    => esc_url_raw(rest_url('wfhsa/v1')),
        'nonce'            => wp_create_nonce('wp_rest'),
        'siteUrl'          => esc_url(home_url('/')),
        'themeVersion'     => WFHSAWEB_THEME_VERSION,
        'targetSyncUrl'    => 'https://wfhsa.com/new/',
    ));
}
add_action('wp_enqueue_scripts', 'wfhsaweb_theme_scripts');

/**
 * Custom Admin Columns for Hygiene Assessments
 */
function wfhsaweb_assessment_columns($columns) {
    $new_cols = array();
    $new_cols['cb'] = $columns['cb'];
    $new_cols['title'] = __('Audit Reference', 'wfhsaweb');
    $new_cols['customer'] = __('Customer', 'wfhsaweb');
    $new_cols['facility'] = __('Ablution Facility', 'wfhsaweb');
    $new_cols['score'] = __('Compliance Score', 'wfhsaweb');
    $new_cols['status'] = __('Audit Status', 'wfhsaweb');
    $new_cols['date'] = $columns['date'];
    return $new_cols;
}
add_filter('manage_wfhsa_assessment_posts_columns', 'wfhsaweb_assessment_columns');

function wfhsaweb_assessment_custom_column($column, $post_id) {
    switch ($column) {
        case 'customer':
            $cust = get_post_meta($post_id, 'wfhsa_customer', true);
            echo esc_html($cust ? $cust : 'Letaba Crushers (Pty) Ltd');
            break;
        case 'facility':
            $fac = get_post_meta($post_id, 'wfhsa_facility', true);
            echo esc_html($fac ? $fac : 'Male Ablution Block 1');
            break;
        case 'score':
            $score = get_post_meta($post_id, 'wfhsa_score', true);
            if ($score === '') $score = 85;
            $color = ($score >= 80) ? '#10B981' : (($score >= 60) ? '#F59E0B' : '#D32F2F');
            echo '<span style="font-weight:800; color:' . esc_attr($color) . ';">' . esc_html($score) . '%</span>';
            break;
        case 'status':
            $st = get_post_meta($post_id, 'wfhsa_status', true);
            echo '<span class="badge" style="background:#202330; padding:2px 8px; border-radius:4px; font-size:11px; color:#FF6D00;">' . esc_html($st ? $st : 'SYNCED') . '</span>';
            break;
    }
}
add_action('manage_wfhsa_assessment_posts_custom_column', 'wfhsaweb_assessment_custom_column', 10, 2);
