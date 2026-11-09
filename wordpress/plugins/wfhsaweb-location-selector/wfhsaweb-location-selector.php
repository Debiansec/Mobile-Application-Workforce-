<?php
/**
 * Plugin Name: WFHSA Web — Customer, Site & Facility Selector Wizard
 * Plugin URI: https://wfhsa.com/new/
 * Description: 3-step interactive location selector for WFHSA Web & WorkForce Health SA: 1. Select Authorized Customer, 2. Select Site & Property, 3. Select Ablution Facility & Launch Inspection Checklist.
 * Version: 1.3.0
 * Author: WorkForce Health SA / WFHSA Web Team
 * Author URI: https://wfhsa.com
 * License: Proprietary
 * Text Domain: wfhsaweb-location-selector
 */

if (!defined('ABSPATH')) {
    exit;
}

class WFHSAWeb_Location_Selector {

    public function __construct() {
        add_action('init', array($this, 'register_shortcodes'));
        add_action('rest_api_init', array($this, 'register_rest_routes'));
    }

    public function register_shortcodes() {
        add_shortcode('wfhsaweb_location_selector', array($this, 'render_location_wizard'));
        add_shortcode('mobiworkforce_location_selector', array($this, 'render_location_wizard'));
        add_shortcode('wfhsa_location_selector', array($this, 'render_location_wizard'));
    }

    public function register_rest_routes() {
        register_rest_route('wfhsaweb/v1', '/locations/hierarchy', array(
            'methods'  => 'GET',
            'callback' => array($this, 'rest_get_hierarchy'),
            'permission_callback' => '__return_true',
        ));

        register_rest_route('mobiworkforce/v1', '/locations/hierarchy', array(
            'methods'  => 'GET',
            'callback' => array($this, 'rest_get_hierarchy'),
            'permission_callback' => '__return_true',
        ));
    }

    public function rest_get_hierarchy() {
        return rest_ensure_response(array(
            'customers' => array(
                array(
                    'id'   => 'cust_letaba',
                    'name' => 'Letaba Crushers (Pty) Ltd',
                    'code' => 'LC-014466',
                    'sites' => array(
                        array(
                            'id'       => 'site_letaba_main',
                            'name'     => 'Letaba Crushers - Main Quarry & Plant',
                            'property' => 'Farm Tarentaalrant 24LT, Tzaneen, Limpopo',
                            'mine_no'  => '14466',
                            'facilities' => array(
                                array('id' => 'fac_letaba_ablution_1', 'name' => 'Male Ablution Block 1 (Plant Change House)', 'type' => 'Ablution Male'),
                                array('id' => 'fac_letaba_ablution_2', 'name' => 'Primary Crusher Pit Restroom', 'type' => 'Ablution Male'),
                                array('id' => 'fac_letaba_workshop',   'name' => 'Heavy Engineering Workshop Facility', 'type' => 'Workshop'),
                            )
                        )
                    )
                ),
                array(
                    'id'   => 'cust_marula',
                    'name' => 'Marula Platinum Mine (Pty) Ltd',
                    'code' => 'MP-089921',
                    'sites' => array(
                        array(
                            'id'       => 'site_marula_shaft',
                            'name'     => 'Marula Shaft 2 Complex',
                            'property' => 'Clapham Section, Burgersfort, Limpopo',
                            'mine_no'  => '08992',
                            'facilities' => array(
                                array('id' => 'fac_marula_underground_1', 'name' => 'Underground Waiting Station Latrines', 'type' => 'Ablution Male'),
                                array('id' => 'fac_marula_surface_lamp',  'name' => 'Surface Lamp Room Change House', 'type' => 'Ablution Male'),
                            )
                        )
                    )
                )
            )
        ));
    }

    public function render_location_wizard() {
        ob_start();
        ?>
        <div id="wfhsaweb_location_wizard_root" style="background:#171922; border:1px solid #2E3244; border-radius:10px; padding:16px;">
            <div style="display:flex; flex-direction:column; gap:12px;">
                <!-- Step 1: Customer -->
                <div>
                    <label style="font-size:0.75rem; font-weight:800; color:#FF6D00; text-transform:uppercase; display:block; margin-bottom:4px;">
                        1. Mining Company / Customer
                    </label>
                    <select id="wfhsaweb_sel_customer" style="width:100%; background:#12141C; border:1px solid #2E3244; color:#FFFFFF; padding:8px 12px; border-radius:6px; font-size:0.85rem;">
                        <option value="cust_letaba">Letaba Crushers (Pty) Ltd &bull; Tzaneen Quarry #14466</option>
                        <option value="cust_marula">Marula Platinum Mine (Pty) Ltd &bull; Burgersfort #08992</option>
                    </select>
                </div>

                <!-- Step 2: Site -->
                <div>
                    <label style="font-size:0.75rem; font-weight:800; color:#FF6D00; text-transform:uppercase; display:block; margin-bottom:4px;">
                        2. Mining Property / Site
                    </label>
                    <select id="wfhsaweb_sel_site" style="width:100%; background:#12141C; border:1px solid #2E3244; color:#FFFFFF; padding:8px 12px; border-radius:6px; font-size:0.85rem;">
                        <option value="site_letaba_main">Main Quarry & Crushing Plant &bull; Farm Tarentaalrant 24LT</option>
                    </select>
                </div>

                <!-- Step 3: Facility -->
                <div>
                    <label style="font-size:0.75rem; font-weight:800; color:#FF6D00; text-transform:uppercase; display:block; margin-bottom:4px;">
                        3. Specific Ablution Facility
                    </label>
                    <select id="wfhsaweb_sel_facility" style="width:100%; background:#12141C; border:1px solid #2E3244; color:#FFFFFF; padding:8px 12px; border-radius:6px; font-size:0.85rem;">
                        <option value="fac_letaba_ablution_1">Monthly Ablution Facilities Checklist — Male (Block 1)</option>
                        <option value="fac_letaba_ablution_2">Primary Crusher Pit Restroom (Block 2)</option>
                    </select>
                </div>

                <!-- Confirm & Load Button -->
                <button type="button" id="wfhsaweb_btn_confirm_loc" class="wfhsa-btn wfhsa-btn-primary" style="margin-top:6px; width:100%; justify-content:center; padding:10px; font-size:0.85rem; font-weight:800;">
                    ✓ Set Facility & Load 51-Point Inspection Checklist
                </button>
                <div id="wfhsaweb_loc_status" style="font-size:0.75rem; color:#10B981; text-align:center;"></div>
            </div>
        </div>

        <script>
        document.addEventListener('DOMContentLoaded', function() {
            var btn = document.getElementById('wfhsaweb_btn_confirm_loc');
            var status = document.getElementById('wfhsaweb_loc_status');
            if (btn) {
                btn.addEventListener('click', function() {
                    status.innerHTML = '✓ Facility confirmed. Ready to inspect.';
                    var checklistEl = document.getElementById('mobi_inspection_checklist_container') || document.getElementById('wfhsa_checklist_root');
                    if (checklistEl) {
                        checklistEl.scrollIntoView({ behavior: 'smooth' });
                    }
                });
            }
        });
        </script>
        <?php
        return ob_get_clean();
    }
}

new WFHSAWeb_Location_Selector();
