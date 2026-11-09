<?php
/**
 * Plugin Name: Portal WFHSA — Select Authorized User & Role Management
 * Plugin URI: https://wfhsa.com/new/
 * Description: Role-based access control and user authorization plugin for Portal WFHSA & WorkForce Health SA Occupational Hygiene Platform. Implements Field Assessor, Occupational Hygienist (SAIOH Registered), Mine Management, and System Administrator roles.
 * Version: 1.3.0
 * Author: WorkForce Health SA / Portal WFHSA Team
 * Author URI: https://wfhsa.com
 * License: Proprietary
 * Text Domain: portalwfhsa-user-roles
 */

if (!defined('ABSPATH')) {
    exit;
}

class WFHSAWeb_User_Roles {

    public function __construct() {
        register_activation_hook(__FILE__, array($this, 'activate_roles'));
        add_action('init', array($this, 'register_shortcodes'));
        add_action('wp_ajax_portalwfhsa_switch_role', array($this, 'ajax_switch_role'));
        add_action('wp_ajax_mobiworkforce_switch_role', array($this, 'ajax_switch_role'));
        add_action('wp_ajax_wfhsa_switch_role', array($this, 'ajax_switch_role'));
    }

    public function activate_roles() {
        // 1. Field Assessor
        add_role('portalwfhsa_field_assessor', 'Field Assessor (Occupational Hygiene Assistant)', array(
            'read' => true,
            'portalwfhsa_capture_inspections' => true,
            'portalwfhsa_save_drafts' => true,
            'portalwfhsa_submit_inspections' => true,
            'portalwfhsa_upload_evidence' => true,
        ));

        // 2. Occupational Hygienist (SAIOH Registered)
        add_role('portalwfhsa_hygienist', 'Occupational Hygienist (SAIOH Registered)', array(
            'read' => true,
            'portalwfhsa_capture_inspections' => true,
            'portalwfhsa_review_inspections' => true,
            'portalwfhsa_approve_reports' => true,
            'portalwfhsa_return_corrections' => true,
            'portalwfhsa_evaluate_findings' => true,
        ));

        // 3. Mine Management / Client Representative
        add_role('portalwfhsa_mine_manager', 'Mine Management / Client Representative', array(
            'read' => true,
            'portalwfhsa_view_site_reports' => true,
            'portalwfhsa_acknowledge_findings' => true,
            'portalwfhsa_track_remedial' => true,
            'portalwfhsa_sign_inspections' => true,
        ));

        // 4. System Administrator
        add_role('portalwfhsa_system_admin', 'System Administrator (Portal WFHSA)', array(
            'read' => true,
            'edit_posts' => true,
            'delete_posts' => true,
            'portalwfhsa_capture_inspections' => true,
            'portalwfhsa_review_inspections' => true,
            'portalwfhsa_approve_reports' => true,
            'portalwfhsa_manage_customers' => true,
            'portalwfhsa_manage_templates' => true,
            'portalwfhsa_view_audit_logs' => true,
        ));
    }

    public function register_shortcodes() {
        add_shortcode('portalwfhsa_role_switcher', array($this, 'render_role_switcher'));
        add_shortcode('mobiworkforce_role_switcher', array($this, 'render_role_switcher'));
        add_shortcode('wfhsa_role_switcher', array($this, 'render_role_switcher'));
    }

    public function render_role_switcher() {
        ob_start();
        $current_user = wp_get_current_user();
        $roles = (array) $current_user->roles;
        $active_role = !empty($roles) ? $roles[0] : 'portalwfhsa_field_assessor';
        ?>
        <div class="portalwfhsa-role-card" style="background: #171922; border: 1px solid #2E3244; border-radius: 12px; padding: 22px; color: #F8FAFC;">
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
                <div style="background: #202330; border: 1px solid <?php echo ($active_role === 'portalwfhsa_field_assessor' || $active_role === 'mobiworkforce_field_assessor') ? '#FF6D00' : '#2E3244'; ?>; border-radius: 8px; padding: 14px;">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px;">
                        <span style="font-weight: 800; font-size: 0.95rem; color: #FFFFFF;">Field Assessor</span>
                        <span class="badge" style="background: #FF6D00; color: white; padding: 2px 6px; border-radius: 4px; font-size: 10px; font-weight: bold;">FIELD</span>
                    </div>
                    <div style="font-size: 0.8rem; color: #94A3B8; line-height: 1.4; margin-bottom: 12px;">
                        Conducts on-site inspections, records compliance values, dictates voice notes, captures geotags, and captures photo evidence.
                    </div>
                    <button type="button" onclick="portalwfhsaSetRole('portalwfhsa_field_assessor')" class="wfhsa-btn wfhsa-btn-outline" style="width: 100%; font-size: 0.8rem; padding: 6px 12px; justify-content: center;">
                        Select Role
                    </button>
                </div>

                <!-- Role 2 -->
                <div style="background: #202330; border: 1px solid <?php echo ($active_role === 'portalwfhsa_hygienist' || $active_role === 'mobiworkforce_hygienist') ? '#FF6D00' : '#2E3244'; ?>; border-radius: 8px; padding: 14px;">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px;">
                        <span style="font-weight: 800; font-size: 0.95rem; color: #FFFFFF;">SAIOH Hygienist</span>
                        <span class="badge" style="background: #10B981; color: white; padding: 2px 6px; border-radius: 4px; font-size: 10px; font-weight: bold;">SAIOH</span>
                    </div>
                    <div style="font-size: 0.8rem; color: #94A3B8; line-height: 1.4; margin-bottom: 12px;">
                        Reviews submitted audits, evaluates critical hygiene risks, issues remedial instructions, and signs official certificates.
                    </div>
                    <button type="button" onclick="portalwfhsaSetRole('portalwfhsa_hygienist')" class="wfhsa-btn wfhsa-btn-outline" style="width: 100%; font-size: 0.8rem; padding: 6px 12px; justify-content: center;">
                        Select Role
                    </button>
                </div>

                <!-- Role 3 -->
                <div style="background: #202330; border: 1px solid <?php echo ($active_role === 'portalwfhsa_mine_manager' || $active_role === 'mobiworkforce_mine_manager') ? '#FF6D00' : '#2E3244'; ?>; border-radius: 8px; padding: 14px;">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px;">
                        <span style="font-weight: 800; font-size: 0.95rem; color: #FFFFFF;">Mine Manager</span>
                        <span class="badge" style="background: #3B82F6; color: white; padding: 2px 6px; border-radius: 4px; font-size: 10px; font-weight: bold;">CLIENT</span>
                    </div>
                    <div style="font-size: 0.8rem; color: #94A3B8; line-height: 1.4; margin-bottom: 12px;">
                        Views executive scorecards, counter-signs completed certificates, and assigns maintenance contractors for remedial items.
                    </div>
                    <button type="button" onclick="portalwfhsaSetRole('portalwfhsa_mine_manager')" class="wfhsa-btn wfhsa-btn-outline" style="width: 100%; font-size: 0.8rem; padding: 6px 12px; justify-content: center;">
                        Select Role
                    </button>
                </div>
            </div>
            <div id="portalwfhsa_role_notice" style="margin-top: 14px; font-size: 0.8rem; color: #10B981; text-align: center;"></div>
        </div>

        <script>
        function portalwfhsaSetRole(role) {
            var notice = document.getElementById('portalwfhsa_role_notice');
            if (notice) {
                notice.innerText = 'Active persona switched to: ' + role.replace('portalwfhsa_', '').replace('_', ' ').toUpperCase();
            }
        }
        </script>
        <?php
        return ob_get_clean();
    }

    public function ajax_switch_role() {
        check_ajax_referer('portalwfhsa_nonce', 'security');
        $role = isset($_POST['role']) ? sanitize_text_field($_POST['role']) : '';
        wp_send_json_success(array('role' => $role, 'switched' => true));
    }
}

new WFHSAWeb_User_Roles();
