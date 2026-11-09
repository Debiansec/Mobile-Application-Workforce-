<?php
/**
 * Plugin Name: MobiWorkforce — Complete Occupational Hygiene Suite
 * Plugin URI: https://wfhsa.com/new/
 * Description: Master Enterprise Plugin Suite for MobiWorkforce & WorkForce Health SA. Combines Authorized User & Role Management, 3-Step Customer & Facility Location Selector, 51-Question Occupational Hygiene Inspection Engine, Cloud Sync REST Endpoints (/new/wp-json/mobiworkforce/v1/sync), and Print/PDF Audit Report Generator.
 * Version: 1.2.0
 * Author: WorkForce Health SA / MobiWorkforce Team
 * Author URI: https://wfhsa.com
 * License: Proprietary
 * Text Domain: mobiworkforce-suite
 */

if (!defined('ABSPATH')) {
    exit;
}

define('MOBIWORKFORCE_SUITE_VERSION', '1.2.0');
define('MOBIWORKFORCE_SUITE_PATH', plugin_dir_path(__FILE__));
define('MOBIWORKFORCE_SUITE_URL', plugin_dir_url(__FILE__));

class MobiWorkforce_Complete_Suite {

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
        add_role('mobiworkforce_field_assessor', 'Field Assessor (Occupational Hygiene Assistant)', array(
            'read' => true,
            'mobiworkforce_capture_inspections' => true,
            'mobiworkforce_save_drafts' => true,
            'mobiworkforce_submit_inspections' => true,
            'mobiworkforce_upload_evidence' => true,
        ));

        // 2. Occupational Hygienist (SAIOH Registered)
        add_role('mobiworkforce_hygienist', 'Occupational Hygienist (SAIOH Registered)', array(
            'read' => true,
            'mobiworkforce_capture_inspections' => true,
            'mobiworkforce_review_inspections' => true,
            'mobiworkforce_approve_reports' => true,
            'mobiworkforce_return_corrections' => true,
            'mobiworkforce_evaluate_findings' => true,
        ));

        // 3. Mine Management / Client Representative
        add_role('mobiworkforce_mine_manager', 'Mine Management / Client Representative', array(
            'read' => true,
            'mobiworkforce_view_site_reports' => true,
            'mobiworkforce_acknowledge_findings' => true,
            'mobiworkforce_track_remedial' => true,
            'mobiworkforce_sign_inspections' => true,
        ));

        // 4. System Administrator
        add_role('mobiworkforce_system_admin', 'System Administrator (MobiWorkforce)', array(
            'read' => true,
            'edit_posts' => true,
            'delete_posts' => true,
            'mobiworkforce_capture_inspections' => true,
            'mobiworkforce_review_inspections' => true,
            'mobiworkforce_approve_reports' => true,
            'mobiworkforce_manage_customers' => true,
            'mobiworkforce_manage_templates' => true,
            'mobiworkforce_view_audit_logs' => true,
        ));
    }

    public function init_modules() {
        $parent_dir = dirname(plugin_dir_path(__FILE__));

        // 1. Load Sync Engine
        if (!class_exists('MobiWorkforce_Sync_Engine')) {
            $sync_file = $parent_dir . '/mobiworkforce-sync/mobiworkforce-sync.php';
            if (file_exists($sync_file)) {
                require_once $sync_file;
            }
        }

        // 2. Load User Roles
        if (!class_exists('MobiWorkforce_User_Roles')) {
            $user_roles_file = $parent_dir . '/mobiworkforce-user-roles/mobiworkforce-user-roles.php';
            if (file_exists($user_roles_file)) {
                require_once $user_roles_file;
            }
        }

        // 3. Load Location Selector
        if (!class_exists('MobiWorkforce_Location_Selector')) {
            $loc_file = $parent_dir . '/mobiworkforce-location-selector/mobiworkforce-location-selector.php';
            if (file_exists($loc_file)) {
                require_once $loc_file;
            }
        }

        // 4. Load Inspections Engine
        if (!class_exists('MobiWorkforce_Inspections_Engine')) {
            $insp_file = $parent_dir . '/mobiworkforce-inspections/mobiworkforce-inspections.php';
            if (file_exists($insp_file)) {
                require_once $insp_file;
            }
        }

        // 5. Load Reports PDF
        if (!class_exists('MobiWorkforce_Reports_PDF')) {
            $rep_file = $parent_dir . '/mobiworkforce-reports-pdf/mobiworkforce-reports-pdf.php';
            if (file_exists($rep_file)) {
                require_once $rep_file;
            }
        }
    }

    public function admin_activation_notice() {
        $screen = get_current_screen();
        if ($screen && (strpos($screen->id, 'mobiworkforce') !== false || strpos($screen->id, 'wfhsa') !== false)) {
            ?>
            <div class="notice notice-success is-dismissible" style="border-left-color: #FF6D00;">
                <p><strong>MOBIWORKFORCE Complete Suite v1.2.0</strong> is active. Connected to cloud API endpoint <code>https://wfhsa.com/new/wp-json/mobiworkforce/v1</code> and Android companion inspection app.</p>
            </div>
            <?php
        }
    }
}

MobiWorkforce_Complete_Suite::get_instance();
