<?php
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
    $widgets_manager->register(new \Portalwfhsa_Dashboard_Widget());
    $widgets_manager->register(new \Portalwfhsa_Checklist_Widget());
    $widgets_manager->register(new \Portalwfhsa_Location_Selector_Widget());
    $widgets_manager->register(new \Portalwfhsa_Remedial_Actions_Widget());
});
