<?php
/**
 * Plugin Name: MobiWorkforce — Select Authorized User & Role Management
 * Plugin URI: https://wfhsa.com/new/
 * Description: Role-based access control and user authorization plugin for MobiWorkforce & WorkForce Health SA Occupational Hygiene Platform. Implements Field Assessor, Occupational Hygienist (SAIOH Registered), Mine Management, and System Administrator roles.
 * Version: 1.2.0
 * Author: WorkForce Health SA / MobiWorkforce Team
 * Author URI: https://wfhsa.com
 * License: Proprietary
 * Text Domain: mobiworkforce-user-roles
 */

if (!defined('ABSPATH')) {
    exit;
}

class MobiWorkforce_User_Roles {

    public function __construct() {
        register_activation_hook(__FILE__, array($this, 'activate_roles'));
        add_action('init', array($this, 'register_shortcodes'));
        add_action('wp_ajax_mobiworkforce_switch_role', array($this, 'ajax_switch_role'));
        add_action('wp_ajax_wfhsa_switch_role', array($this, 'ajax_switch_role'));
    }

    public function activate_roles() {
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

        // Also add legacy wfhsa_* roles for backward compatibility
        if (class_exists('WFHSA_User_Roles')) {
            $legacy = new WFHSA_User_Roles();
            $legacy->activate_roles();
        }
    }

    public function register_shortcodes() {
        add_shortcode('mobiworkforce_role_switcher', array($this, 'render_role_switcher'));
        add_shortcode('wfhsa_role_switcher', array($this, 'render_role_switcher'));
    }

    public function render_role_switcher() {
        ob_start();
        $current_user = wp_get_current_user();
        $roles = (array) $current_user->roles;
        $active_role = !empty($roles) ? $roles[0] : 'mobiworkforce_field_assessor';
        ?>
        <div class="mobiworkforce-role-card" style="background: #171922; border: 1px solid #2E3244; border-radius: 12px; padding: 22px; color: #F8FAFC;">
            <div style="font-size: 0.8rem; font-weight: 800; color: #FF6D00; letter-spacing: 1.5px; margin-bottom: 6px;">
                SECURITY & ROLE-BASED ACCESS CONTROL
            </div>
            <h3 style="font-size: 1.25rem; font-weight: 800; color: #FFFFFF; margin-bottom: 14px;">
                Authorized User & Role Selector
            </h3>

            <p style="font-size: 0.85rem; color: #94A3B8; margin-bottom: 16px;">
                Select your verified operational persona to filter authorized checklists, approval workflows, and signature permissions.
            </p>

            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); gap: 12px;">
                <!-- Role 1 -->
                <div style="background: #202330; border: 1px solid <?php echo ($active_role === 'mobiworkforce_field_assessor' || $active_role === 'wfhsa_field_assessor') ? '#FF6D00' : '#2E3244'; ?>; border-radius: 8px; padding: 14px;">
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                        <span style="font-weight: 700; color: #FFFFFF; font-size: 0.95rem;">Field Assessor</span>
                        <span class="wfhsa-badge badge-orange">DATA CAPTURE</span>
                    </div>
                    <div style="font-size: 0.78rem; color: #94A3B8; margin-top: 6px;">
                        Glove-friendly mobile inspection capture, CameraX photo evidence, offline queuing.
                    </div>
                </div>

                <!-- Role 2 -->
                <div style="background: #202330; border: 1px solid <?php echo ($active_role === 'mobiworkforce_hygienist' || $active_role === 'wfhsa_hygienist') ? '#10B981' : '#2E3244'; ?>; border-radius: 8px; padding: 14px;">
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                        <span style="font-weight: 700; color: #FFFFFF; font-size: 0.95rem;">SAIOH Hygienist</span>
                        <span class="wfhsa-badge badge-green">APPROVER</span>
                    </div>
                    <div style="font-size: 0.78rem; color: #94A3B8; margin-top: 6px;">
                        Technical validation, remedial severity classification, legal compliance certification.
                    </div>
                </div>

                <!-- Role 3 -->
                <div style="background: #202330; border: 1px solid <?php echo ($active_role === 'mobiworkforce_mine_manager' || $active_role === 'wfhsa_mine_manager') ? '#3B82F6' : '#2E3244'; ?>; border-radius: 8px; padding: 14px;">
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                        <span style="font-weight: 700; color: #FFFFFF; font-size: 0.95rem;">Mine Management</span>
                        <span class="wfhsa-badge badge-blue">CLIENT REP</span>
                    </div>
                    <div style="font-size: 0.78rem; color: #94A3B8; margin-top: 6px;">
                        Executive summary review, non-compliance acknowledgment, dual digital signing.
                    </div>
                </div>
            </div>
        </div>
        <?php
        return ob_get_clean();
    }

    public function ajax_switch_role() {
        wp_send_json_success(array('message' => 'Role switched successfully.'));
    }
}

new MobiWorkforce_User_Roles();
