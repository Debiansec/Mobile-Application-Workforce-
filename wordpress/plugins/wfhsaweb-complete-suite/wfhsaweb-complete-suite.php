<?php
/**
 * Plugin Name: WFHSA Web — Complete Occupational Hygiene Suite
 * Plugin URI: https://wfhsa.com/new/
 * Description: Master Enterprise Plugin Suite for WFHSA Web & WorkForce Health SA. Combines Authorized User & Role Management, 3-Step Customer & Facility Location Selector, 51-Question Occupational Hygiene Inspection Engine with Voice-to-Text & GPS Geotags, Cloud Sync REST Endpoints (/new/wp-json/wfhsaweb/v1/sync), and Print/PDF Audit Report Generator.
 * Version: 1.3.0
 * Author: WorkForce Health SA / WFHSA Web Team
 * Author URI: https://wfhsa.com
 * License: Proprietary
 * Text Domain: wfhsaweb-suite
 */

if (!defined('ABSPATH')) {
    exit;
}

define('WFHSAWEB_SUITE_VERSION', '1.3.0');
define('WFHSAWEB_SUITE_PATH', plugin_dir_path(__FILE__));
define('WFHSAWEB_SUITE_URL', plugin_dir_url(__FILE__));

class WFHSAWeb_Complete_Suite {

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
        $this->ensure_roles();
        flush_rewrite_rules();
    }

    public function on_deactivate() {
        flush_rewrite_rules();
    }

    public function ensure_roles() {
        // 1. Field Assessor
        add_role('wfhsaweb_field_assessor', 'Field Assessor (Occupational Hygiene Assistant)', array(
            'read' => true,
            'wfhsaweb_capture_inspections' => true,
            'wfhsaweb_save_drafts' => true,
            'wfhsaweb_submit_inspections' => true,
            'wfhsaweb_upload_evidence' => true,
        ));

        // 2. Occupational Hygienist (SAIOH Registered)
        add_role('wfhsaweb_hygienist', 'Occupational Hygienist (SAIOH Registered)', array(
            'read' => true,
            'wfhsaweb_capture_inspections' => true,
            'wfhsaweb_review_inspections' => true,
            'wfhsaweb_approve_reports' => true,
            'wfhsaweb_return_corrections' => true,
            'wfhsaweb_evaluate_findings' => true,
        ));

        // 3. Mine Management / Client Representative
        add_role('wfhsaweb_mine_manager', 'Mine Management / Client Representative', array(
            'read' => true,
            'wfhsaweb_view_site_reports' => true,
            'wfhsaweb_acknowledge_findings' => true,
            'wfhsaweb_track_remedial' => true,
            'wfhsaweb_sign_inspections' => true,
        ));

        // 4. System Administrator
        add_role('wfhsaweb_system_admin', 'System Administrator (WFHSA Web)', array(
            'read' => true,
            'edit_posts' => true,
            'delete_posts' => true,
            'wfhsaweb_capture_inspections' => true,
            'wfhsaweb_review_inspections' => true,
            'wfhsaweb_approve_reports' => true,
            'wfhsaweb_manage_customers' => true,
            'wfhsaweb_manage_templates' => true,
            'wfhsaweb_view_audit_logs' => true,
        ));
    }

    public function init_modules() {
        $parent_dir = dirname(plugin_dir_path(__FILE__));

        // 1. Load Sync Engine
        if (!class_exists('WFHSAWeb_Sync_Engine')) {
            $sync_file = $parent_dir . '/wfhsaweb-sync/wfhsaweb-sync.php';
            if (file_exists($sync_file)) {
                require_once $sync_file;
            }
        }

        // 2. Load User Roles
        if (!class_exists('WFHSAWeb_User_Roles')) {
            $user_roles_file = $parent_dir . '/wfhsaweb-user-roles/wfhsaweb-user-roles.php';
            if (file_exists($user_roles_file)) {
                require_once $user_roles_file;
            }
        }

        // 3. Load Location Selector
        if (!class_exists('WFHSAWeb_Location_Selector')) {
            $loc_file = $parent_dir . '/wfhsaweb-location-selector/wfhsaweb-location-selector.php';
            if (file_exists($loc_file)) {
                require_once $loc_file;
            }
        }

        // 4. Load Inspections Engine
        if (!class_exists('WFHSAWeb_Inspections_Engine')) {
            $insp_file = $parent_dir . '/wfhsaweb-inspections/wfhsaweb-inspections.php';
            if (file_exists($insp_file)) {
                require_once $insp_file;
            }
        }

        // 5. Load Reports PDF
        if (!class_exists('WFHSAWeb_Reports_PDF')) {
            $rep_file = $parent_dir . '/wfhsaweb-reports-pdf/wfhsaweb-reports-pdf.php';
            if (file_exists($rep_file)) {
                require_once $rep_file;
            }
        }
    }

    public function admin_activation_notice() {
        $screen = get_current_screen();
        if ($screen && (strpos($screen->id, 'wfhsaweb') !== false || strpos($screen->id, 'wfhsa') !== false)) {
            ?>
            <div class="notice notice-success is-dismissible" style="border-left-color: #FF6D00;">
                <p><strong>WFHSA WEB Complete Suite v1.3.0</strong> is active. Connected to cloud API endpoint <code>https://wfhsa.com/new/wp-json/wfhsaweb/v1</code> and Android companion inspection app.</p>
            </div>
            <?php
        }
    }
}

WFHSAWeb_Complete_Suite::get_instance();
