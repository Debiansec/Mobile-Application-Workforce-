<?php
/**
 * Plugin Name: MobiWorkforce — Customer, Site & Facility Selector Wizard
 * Plugin URI: https://wfhsa.com/new/
 * Description: 3-step interactive location selector for MobiWorkforce & WorkForce Health SA: 1. Select Authorized Customer, 2. Select Site & Property, 3. Select Ablution Facility & Launch Inspection Checklist.
 * Version: 1.2.0
 * Author: WorkForce Health SA / MobiWorkforce Team
 * Author URI: https://wfhsa.com
 * License: Proprietary
 * Text Domain: mobiworkforce-location-selector
 */

if (!defined('ABSPATH')) {
    exit;
}

class MobiWorkforce_Location_Selector {

    public function __construct() {
        add_action('init', array($this, 'register_shortcodes'));
        add_action('rest_api_init', array($this, 'register_rest_routes'));
    }

    public function register_shortcodes() {
        add_shortcode('mobiworkforce_location_selector', array($this, 'render_location_wizard'));
        add_shortcode('wfhsa_location_selector', array($this, 'render_location_wizard'));
    }

    public function register_rest_routes() {
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
                    'code' => 'MPM-099201',
                    'sites' => array(
                        array(
                            'id'       => 'site_marula_shaft',
                            'name'     => 'Driekop Shaft & Concentrator',
                            'property' => 'Farm Driekop 253KT, Limpopo',
                            'mine_no'  => '25300',
                            'facilities' => array(
                                array('id' => 'fac_marula_ablution_a', 'name' => 'Surface Change House (Male North)', 'type' => 'Ablution Male'),
                                array('id' => 'fac_marula_underground', 'name' => 'Level 3 Refuge & Sanitation Station', 'type' => 'Underground'),
                            )
                        )
                    )
                ),
            )
        ));
    }

    public function render_location_wizard() {
        ob_start();
        ?>
        <div class="mobiworkforce-wizard-card" style="background: #171922; border: 1px solid #2E3244; border-radius: 12px; padding: 24px; color: #F8FAFC;">
            <div style="font-size: 0.8rem; font-weight: 800; color: #FF6D00; letter-spacing: 1.5px; margin-bottom: 6px;">
                STEP-BY-STEP INSPECTION INITIATION
            </div>
            <h3 style="font-size: 1.25rem; font-weight: 800; color: #FFFFFF; margin-bottom: 16px;">
                Customer, Site & Facility Selection
            </h3>

            <!-- Step 1: Customer Selection -->
            <div style="margin-bottom: 18px;">
                <label style="display: block; font-size: 0.85rem; font-weight: 700; color: #FF6D00; margin-bottom: 6px; text-transform: uppercase;">
                    1. Select Authorized Customer
                </label>
                <select id="mobi_sel_customer" style="width: 100%; background: #202330; border: 1px solid #2E3244; color: #FFFFFF; padding: 12px 14px; border-radius: 8px; font-size: 0.95rem; font-weight: 600;">
                    <option value="cust_letaba">Letaba Crushers (Pty) Ltd (LC-014466)</option>
                    <option value="cust_marula">Marula Platinum Mine (Pty) Ltd (MPM-099201)</option>
                    <option value="cust_phalaborwa">Palabora Copper Mining Company (PMC-004312)</option>
                </select>
            </div>

            <!-- Step 2: Site & Property Selection -->
            <div style="margin-bottom: 18px;">
                <label style="display: block; font-size: 0.85rem; font-weight: 700; color: #FF6D00; margin-bottom: 6px; text-transform: uppercase;">
                    2. Select Site & Property
                </label>
                <select id="mobi_sel_site" style="width: 100%; background: #202330; border: 1px solid #2E3244; color: #FFFFFF; padding: 12px 14px; border-radius: 8px; font-size: 0.95rem; font-weight: 600;">
                    <option value="site_letaba_main">Letaba Crushers - Main Quarry & Plant (Farm Tarentaalrant 24LT, Tzaneen, Mine #14466)</option>
                    <option value="site_marula_shaft">Driekop Shaft & Concentrator (Farm Driekop 253KT, Limpopo)</option>
                </select>
                <div id="mobi_site_details" style="margin-top: 8px; background: #12141C; border: 1px solid #2E3244; padding: 10px 14px; border-radius: 6px; font-size: 0.78rem; color: #94A3B8;">
                    <strong>Site Location:</strong> Farm Tarentaalrant 24LT, Tzaneen, Limpopo &bull; <strong>Mine Code:</strong> 14466 &bull; <strong>Commodity:</strong> Aggregate & Crushed Stone
                </div>
            </div>

            <!-- Step 3: Ablution Facility Selection & Launch -->
            <div style="margin-bottom: 22px;">
                <label style="display: block; font-size: 0.85rem; font-weight: 700; color: #FF6D00; margin-bottom: 6px; text-transform: uppercase;">
                    3. Select Ablution Facility & Launch Inspection Checklist
                </label>
                <select id="mobi_sel_facility" style="width: 100%; background: #202330; border: 1px solid #2E3244; color: #FFFFFF; padding: 12px 14px; border-radius: 8px; font-size: 0.95rem; font-weight: 600;">
                    <option value="fac_letaba_ablution_1">Male Ablution Block 1 (Plant Change House) - Monthly Checklist</option>
                    <option value="fac_letaba_ablution_2">Primary Crusher Pit Restroom (Male) - Monthly Checklist</option>
                    <option value="fac_letaba_workshop">Workshop Maintenance Ablution Block - Monthly Checklist</option>
                </select>
            </div>

            <div style="display: flex; gap: 12px; align-items: center; flex-wrap: wrap;">
                <button type="button" id="mobi_btn_launch_checklist" class="wfhsa-btn wfhsa-btn-primary" style="flex: 1; min-height: 48px;">
                    🚀 Launch Inspection Checklist (51 Points)
                </button>
                <span id="mobi_launch_status" style="font-size: 0.82rem; color: #10B981; font-weight: 700;"></span>
            </div>
        </div>

        <script>
        document.addEventListener('DOMContentLoaded', function() {
            var btn = document.getElementById('mobi_btn_launch_checklist');
            var status = document.getElementById('mobi_launch_status');
            if (btn) {
                btn.addEventListener('click', function() {
                    var fac = document.getElementById('mobi_sel_facility').value;
                    status.innerHTML = '✓ Facility confirmed. Checklist loaded below.';
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

new MobiWorkforce_Location_Selector();
