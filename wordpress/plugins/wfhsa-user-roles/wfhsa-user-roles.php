<?php
/**
 * Plugin Name: WORKFORCE HEALTH SA — Authorized User & Role Management
 * Plugin URI: https://wfhsa.com
 * Description: Role-based access control and user authorization plugin for WORKFORCE HEALTH SA Occupational Hygiene Digital Platform. Implements Field Assessor, Occupational Hygienist, Mine Management, and System Administrator roles.
 * Version: 1.0.0
 * Author: GeekForge / WorkForce Health SA
 * Author URI: https://wfhsa.com
 * License: Proprietary
 * Text Domain: wfhsa-user-roles
 */

if (!defined('ABSPATH')) {
    exit;
}

class WFHSA_User_Roles {

    public function __construct() {
        register_activation_hook(__FILE__, array($this, 'activate_roles'));
        add_action('init', array($this, 'register_shortcodes'));
        add_action('rest_api_init', array($this, 'register_rest_routes'));
        add_action('show_user_profile', array($this, 'custom_user_profile_fields'));
        add_action('edit_user_profile', array($this, 'custom_user_profile_fields'));
        add_action('personal_options_update', array($this, 'save_custom_user_profile_fields'));
        add_action('edit_user_profile_update', array($this, 'save_custom_user_profile_fields'));
        add_action('wp_ajax_wfhsa_switch_role', array($this, 'ajax_switch_role'));
    }

    public function activate_roles() {
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

    public function custom_user_profile_fields($user) {
        $reg_no = get_user_meta($user->ID, 'wfhsa_registration_number', true);
        $affiliation = get_user_meta($user->ID, 'wfhsa_mine_affiliation', true);
        ?>
        <h3 style="color: #D32F2F;">WORKFORCE HEALTH SA — Professional Credentials</h3>
        <table class="form-table">
            <tr>
                <th><label for="wfhsa_registration_number">SAIOH / DME Registration No.</label></th>
                <td>
                    <input type="text" name="wfhsa_registration_number" id="wfhsa_registration_number" value="<?php echo esc_attr($reg_no); ?>" class="regular-text" placeholder="e.g. SAIOH ROH 0482 or OH-Asst 1892" />
                    <p class="description">Official South African Institute for Occupational Hygiene or Inspectorate credential.</p>
                </td>
            </tr>
            <tr>
                <th><label for="wfhsa_mine_affiliation">Mine / Company Affiliation</label></th>
                <td>
                    <input type="text" name="wfhsa_mine_affiliation" id="wfhsa_mine_affiliation" value="<?php echo esc_attr($affiliation); ?>" class="regular-text" placeholder="e.g. Letaba Crushers (Pty) Ltd" />
                </td>
            </tr>
        </table>
        <?php
    }

    public function save_custom_user_profile_fields($user_id) {
        if (!current_user_can('edit_user', $user_id)) {
            return false;
        }
        if (isset($_POST['wfhsa_registration_number'])) {
            update_user_meta($user_id, 'wfhsa_registration_number', sanitize_text_field($_POST['wfhsa_registration_number']));
        }
        if (isset($_POST['wfhsa_mine_affiliation'])) {
            update_user_meta($user_id, 'wfhsa_mine_affiliation', sanitize_text_field($_POST['wfhsa_mine_affiliation']));
        }
    }

    public function register_shortcodes() {
        add_shortcode('wfhsa_role_switcher', array($this, 'render_role_switcher'));
    }

    public function render_role_switcher() {
        ob_start();
        ?>
        <div class="wfhsa-role-switcher-box" style="background: #171922; border: 1px solid #2E3244; border-radius: 10px; padding: 18px; color: #F8FAFC;">
            <div style="font-weight: bold; font-size: 1rem; margin-bottom: 6px; color: #FF6D00;">
                Select Authorized User & Role
            </div>
            <p style="font-size: 0.82rem; color: #94A3B8; margin-bottom: 14px;">
                Switch between field assessor and approval roles to test permissions.
            </p>

            <div style="display: flex; flex-direction: column; gap: 10px;">
                <label style="display: flex; align-items: center; gap: 10px; background: #202330; padding: 10px 14px; border-radius: 8px; cursor: pointer; border: 1px solid #2E3244;">
                    <input type="radio" name="wfhsa_demo_role" value="assessor" checked />
                    <div>
                        <div style="font-weight: bold; font-size: 0.9rem;">Nomvula Khumalo (Field Assessor)</div>
                        <div style="font-size: 0.75rem; color: #38BDF8;">SAIOH OH-Asst 1892 • Data capture & offline sync</div>
                    </div>
                </label>

                <label style="display: flex; align-items: center; gap: 10px; background: #202330; padding: 10px 14px; border-radius: 8px; cursor: pointer; border: 1px solid #2E3244;">
                    <input type="radio" name="wfhsa_demo_role" value="hygienist" />
                    <div>
                        <div style="font-weight: bold; font-size: 0.9rem;">Riaan Steyn (Occupational Hygienist)</div>
                        <div style="font-size: 0.75rem; color: #10B981;">SAIOH ROH 0482 • Review findings & authorize final reports</div>
                    </div>
                </label>

                <label style="display: flex; align-items: center; gap: 10px; background: #202330; padding: 10px 14px; border-radius: 8px; cursor: pointer; border: 1px solid #2E3244;">
                    <input type="radio" name="wfhsa_demo_role" value="manager" />
                    <div>
                        <div style="font-weight: bold; font-size: 0.9rem;">Pieter Botha (Mine Manager / Client Rep)</div>
                        <div style="font-size: 0.75rem; color: #F59E0B;">Letaba Crushers (Mine #14466) • Acknowledge & sign findings</div>
                    </div>
                </label>

                <label style="display: flex; align-items: center; gap: 10px; background: #202330; padding: 10px 14px; border-radius: 8px; cursor: pointer; border: 1px solid #2E3244;">
                    <input type="radio" name="wfhsa_demo_role" value="admin" />
                    <div>
                        <div style="font-weight: bold; font-size: 0.9rem;">System Administrator (GeekForge / WFHSA)</div>
                        <div style="font-size: 0.75rem; color: #A855F7;">Full system configuration, customers, sites & templates</div>
                    </div>
                </label>
            </div>

            <div id="wfhsa_role_status" style="margin-top: 14px; font-size: 0.8rem; color: #10B981; font-weight: 600;">
                Active Session: Nomvula Khumalo (Field Assessor)
            </div>
        </div>
        <script>
        document.querySelectorAll('input[name="wfhsa_demo_role"]').forEach(function(radio) {
            radio.addEventListener('change', function() {
                var roleNames = {
                    'assessor': 'Nomvula Khumalo (Field Assessor)',
                    'hygienist': 'Riaan Steyn (Occupational Hygienist - SAIOH ROH 0482)',
                    'manager': 'Pieter Botha (Mine Manager - Letaba Crushers)',
                    'admin': 'System Administrator'
                };
                document.getElementById('wfhsa_role_status').textContent = 'Active Session: ' + roleNames[this.value];
            });
        });
        </script>
        <?php
        return ob_get_clean();
    }

    public function register_rest_routes() {
        register_rest_route('wfhsa/v1', '/users/roles', array(
            'methods' => 'GET',
            'callback' => array($this, 'rest_get_roles'),
            'permission_callback' => '__return_true',
        ));
    }

    public function rest_get_roles() {
        return rest_ensure_response(array(
            array('id' => 'wfhsa_field_assessor', 'name' => 'Occupational Hygiene Assistant / Field Assessor'),
            array('id' => 'wfhsa_hygienist', 'name' => 'Occupational Hygienist (SAIOH Registered)'),
            array('id' => 'wfhsa_mine_manager', 'name' => 'Mine Management / Client Representative'),
            array('id' => 'wfhsa_system_admin', 'name' => 'System Administrator'),
        ));
    }
}

new WFHSA_User_Roles();
