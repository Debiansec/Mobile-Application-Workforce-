<?php
/**
 * Plugin Name: WORKFORCE HEALTH SA — Complete Occupational Hygiene Suite
 * Plugin URI: https://wfhsa.com
 * Description: All-in-One Enterprise Master Plugin for WORKFORCE HEALTH SA. Combines Authorized User & Role Management, 3-Step Customer & Facility Location Selector, 51-Question Occupational Hygiene Inspection Engine, Cloud Sync REST Endpoints, and Print/PDF Audit Report Generator.
 * Version: 1.0.0
 * Author: GeekForge / WorkForce Health SA
 * Author URI: https://wfhsa.com
 * License: Proprietary
 * Text Domain: wfhsa-suite
 */

if (!defined('ABSPATH')) {
    exit;
}

// Define Suite Constants
define('WFHSA_SUITE_VERSION', '1.0.0');
define('WFHSA_SUITE_PATH', plugin_dir_path(__FILE__));
define('WFHSA_SUITE_URL', plugin_dir_url(__FILE__));

class WFHSA_Complete_Suite {

    private static $instance = null;

    public static function get_instance() {
        if (self::$instance === null) {
            self::$instance = new self();
        }
        return self::$instance;
    }

    private function __construct() {
        register_activation_hook(__FILE__, array($this, 'on_activate'));
        register_deactivation_hook(__FILE__, array($this, 'on_deactivate'));

        add_action('plugins_loaded', array($this, 'init_modules'));
        add_action('admin_notices', array($this, 'admin_activation_notice'));
    }

    public function on_activate() {
        // Setup User Roles
        $this->ensure_roles();

        // Flush rewrite rules for custom post types and rest routes
        flush_rewrite_rules();
    }

    public function on_deactivate() {
        flush_rewrite_rules();
    }

    public function ensure_roles() {
        // 1. Field Assessor
        add_role('wfhsa_field_assessor', 'Field Assessor (Occupational Hygiene Assistant)', array(
            'read' => true,
            'wfhsa_capture_inspections' => true,
            'wfhsa_save_drafts' => true,
            'wfhsa_submit_inspections' => true,
            'wfhsa_upload_evidence' => true,
        ));

        // 2. Occupational Hygienist (SAIOH Registered)
        add_role('wfhsa_hygienist', 'Occupational Hygienist (SAIOH Registered)', array(
            'read' => true,
            'wfhsa_capture_inspections' => true,
            'wfhsa_review_inspections' => true,
            'wfhsa_approve_reports' => true,
            'wfhsa_return_corrections' => true,
            'wfhsa_evaluate_findings' => true,
        ));

        // 3. Mine Management / Client Representative
        add_role('wfhsa_mine_manager', 'Mine Management / Client Representative', array(
            'read' => true,
            'wfhsa_view_site_reports' => true,
            'wfhsa_acknowledge_findings' => true,
            'wfhsa_track_remedial' => true,
            'wfhsa_sign_inspections' => true,
        ));

        // 4. System Administrator
        add_role('wfhsa_system_admin', 'System Administrator (WFHSA)', array(
            'read' => true,
            'edit_posts' => true,
            'delete_posts' => true,
            'wfhsa_capture_inspections' => true,
            'wfhsa_review_inspections' => true,
            'wfhsa_approve_reports' => true,
            'wfhsa_manage_customers' => true,
            'wfhsa_manage_templates' => true,
            'wfhsa_view_audit_logs' => true,
        ));
    }

    public function init_modules() {
        // Load modules if standalone counterpart plugins are not currently active
        $plugin_dir = plugin_dir_path(__FILE__);

        // If standalone user roles is not active, load it
        if (!class_exists('WFHSA_User_Roles')) {
            $user_roles_file = dirname($plugin_dir) . '/wfhsa-user-roles/wfhsa-user-roles.php';
            if (file_exists($user_roles_file)) {
                require_once $user_roles_file;
            }
        }

        // If standalone location selector is not active, load it
        if (!class_exists('WFHSA_Location_Selector')) {
            $loc_file = dirname($plugin_dir) . '/wfhsa-location-selector/wfhsa-location-selector.php';
            if (file_exists($loc_file)) {
                require_once $loc_file;
            }
        }

        // If standalone inspections engine is not active, load it
        if (!class_exists('WFHSA_Inspections_Engine')) {
            $insp_file = dirname($plugin_dir) . '/wfhsa-inspections/wfhsa-inspections.php';
            if (file_exists($insp_file)) {
                require_once $insp_file;
            }
        }

        // If standalone reports pdf is not active, load it
        if (!class_exists('WFHSA_Reports_PDF')) {
            $rep_file = dirname($plugin_dir) . '/wfhsa-reports-pdf/wfhsa-reports-pdf.php';
            if (file_exists($rep_file)) {
                require_once $rep_file;
            }
        }
    }

    public function admin_activation_notice() {
        $screen = get_current_screen();
        if ($screen && strpos($screen->id, 'wfhsa') !== false) {
            ?>
            <div class="notice notice-success is-dismissible" style="border-left-color: #D32F2F;">
                <p><strong>WORKFORCE HEALTH SA Complete Suite</strong> is active. Connected to cloud API endpoint <code>https://wfhsa.com/wp-json/wfhsa/v1</code> and Android mobile inspection client.</p>
            </div>
            <?php
        }
    }
}

WFHSA_Complete_Suite::get_instance();
