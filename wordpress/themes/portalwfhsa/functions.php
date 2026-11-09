<?php
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
