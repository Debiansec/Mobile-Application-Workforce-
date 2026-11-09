<?php
/**
 * Plugin Name: WORKFORCE HEALTH SA — Customer, Site & Facility Selector Wizard
 * Plugin URI: https://wfhsa.com
 * Description: 3-step interactive location selector for WORKFORCE HEALTH SA: 1. Select Authorized Customer, 2. Select Site & Property, 3. Select Ablution Facility & Launch Inspection Checklist.
 * Version: 1.0.0
 * Author: GeekForge / WorkForce Health SA
 * Author URI: https://wfhsa.com
 * License: Proprietary
 * Text Domain: wfhsa-location-selector
 */

if (!defined('ABSPATH')) {
    exit;
}

class WFHSA_Location_Selector {

    public function __construct() {
        add_action('init', array($this, 'register_shortcodes'));
        add_action('rest_api_init', array($this, 'register_rest_routes'));
    }

    public function register_shortcodes() {
        add_shortcode('wfhsa_location_selector', array($this, 'render_location_wizard'));
    }

    public function render_location_wizard() {
        ob_start();
        ?>
        <div class="wfhsa-location-wizard-card" style="background: #171922; border: 1px solid #2E3244; border-radius: 12px; padding: 22px; color: #F8FAFC;">
            <div style="font-size: 0.8rem; font-weight: 800; color: #FF6D00; letter-spacing: 1.5px; margin-bottom: 6px;">
                STEP-BY-STEP INSPECTION INITIATION
            </div>
            <h3 style="font-size: 1.25rem; font-weight: 800; color: #FFFFFF; margin-bottom: 16px;">
                Location & Facility Selection
            </h3>

            <!-- Step 1: Customer Selection -->
            <div style="margin-bottom: 18px;">
                <label style="display: block; font-size: 0.85rem; font-weight: 700; color: #FF6D00; margin-bottom: 6px; text-transform: uppercase;">
                    1. Select Authorized Customer
                </label>
                <select id="wfhsa_sel_customer" style="width: 100%; background: #202330; border: 1px solid #2E3244; color: #FFFFFF; padding: 12px 14px; border-radius: 8px; font-size: 0.95rem; font-weight: 600;">
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
                <select id="wfhsa_sel_site" style="width: 100%; background: #202330; border: 1px solid #2E3244; color: #FFFFFF; padding: 12px 14px; border-radius: 8px; font-size: 0.95rem; font-weight: 600;">
                    <option value="site_letaba_main">Letaba Crushers - Main Quarry & Plant (Farm Tarentaalrant 24LT, Tzaneen, Mine #14466)</option>
                    <option value="site_marula_shaft">Driekop Shaft & Concentrator (Farm Driekop 253KT, Limpopo)</option>
                </select>
                <div id="wfhsa_site_details" style="margin-top: 8px; background: #12141C; border: 1px solid #2E3244; padding: 10px 14px; border-radius: 6px; font-size: 0.78rem; color: #94A3B8;">
                    <strong>Mine Manager:</strong> Pieter Botha (Pr.Eng, GCC) &nbsp;|&nbsp; 
                    <strong>Site Hygienist:</strong> Riaan Steyn (SAIOH ROH 0482) &nbsp;|&nbsp; 
                    <strong>Medical Practitioner:</strong> Dr. M. K. Pillay
                </div>
            </div>

            <!-- Step 3: Facility Selection -->
            <div style="margin-bottom: 22px;">
                <label style="display: block; font-size: 0.85rem; font-weight: 700; color: #FF6D00; margin-bottom: 6px; text-transform: uppercase;">
                    3. Select Ablution Facility (Choose from Dropdown box)
                </label>
                <select id="wfhsa_sel_facility" style="width: 100%; background: #202330; border: 1px solid #2E3244; color: #FFFFFF; padding: 12px 14px; border-radius: 8px; font-size: 0.95rem; font-weight: 600;">
                    <option value="fac_male">Monthly ablution facilities checklist *Male (Main Change House)</option>
                    <option value="fac_female">Monthly ablution facilities checklist *Female (Admin Complex)</option>
                    <option value="fac_toilet_basin">Monthly toilet and basin checklist *Male / Female (Workshop Bay 4)</option>
                </select>
                <div style="margin-top: 6px; font-size: 0.75rem; color: #94A3B8;">
                    Approx. Users: ~145 employees &nbsp;|&nbsp; Last Inspection: 2026-09-18
                </div>
            </div>

            <!-- Launch Button -->
            <button id="wfhsa_btn_launch" class="wfhsa-btn wfhsa-btn-primary" style="width: 100%; min-height: 52px; font-size: 1rem; letter-spacing: 0.5px; background: #D32F2F; color: #FFFFFF; border-radius: 8px;">
                🚀 LAUNCH INSPECTION CHECKLIST
            </button>

            <!-- Launch Confirmation & Redirect Box -->
            <div id="wfhsa_launch_status" style="display: none; margin-top: 14px; background: rgba(16, 185, 129, 0.15); border: 1px solid #10B981; border-radius: 8px; padding: 12px 16px; color: #10B981; font-size: 0.85rem;">
                Checklist initialized for <strong>Letaba Crushers (Pty) Ltd</strong>. Redirecting to questionnaire...
            </div>
        </div>

        <script>
        document.getElementById('wfhsa_btn_launch').addEventListener('click', function() {
            var cust = document.getElementById('wfhsa_sel_customer');
            var site = document.getElementById('wfhsa_sel_site');
            var fac = document.getElementById('wfhsa_sel_facility');
            var statusBox = document.getElementById('wfhsa_launch_status');

            statusBox.style.display = 'block';
            statusBox.innerHTML = '✓ Checklist Launched for <strong>' + cust.options[cust.selectedIndex].text + '</strong><br>' +
                                  'Site: ' + site.options[site.selectedIndex].text + '<br>' +
                                  'Module: ' + fac.options[fac.selectedIndex].text + '<br>' +
                                  '<span style="color:#FF6D00;">Connected to Android Room DB & https://wfhsa.com cloud API.</span>';
        });
        </script>
        <?php
        return ob_get_clean();
    }

    public function register_rest_routes() {
        register_rest_route('wfhsa/v1', '/customers', array(
            'methods' => 'GET',
            'callback' => function() {
                return rest_ensure_response(array(
                    array(
                        'id' => 'cust_letaba',
                        'name' => 'Letaba Crushers (Pty) Ltd',
                        'code' => 'LC-014466',
                        'mineManager' => 'Pieter Botha (Pr.Eng, GCC)',
                        'hygienist' => 'Riaan Steyn (SAIOH ROH 0482)',
                        'sites' => array(
                            array(
                                'id' => 'site_letaba_main',
                                'name' => 'Main Quarry & Plant',
                                'property' => 'Farm Tarentaalrant 24LT, Tzaneen, Limpopo',
                                'mineCode' => '14466',
                                'facilities' => array(
                                    array(
                                        'id' => 'fac_male_01',
                                        'name' => 'Monthly ablution facilities checklist *Male (Main Change House)',
                                        'type' => 'MALE_ABLUTION',
                                        'estimatedUsers' => 145,
                                    ),
                                    array(
                                        'id' => 'fac_female_01',
                                        'name' => 'Monthly ablution facilities checklist *Female (Admin Complex)',
                                        'type' => 'FEMALE_ABLUTION',
                                        'estimatedUsers' => 35,
                                    ),
                                    array(
                                        'id' => 'fac_workshop_01',
                                        'name' => 'Monthly toilet and basin checklist *Male / Female (Workshop Bay 4)',
                                        'type' => 'TOILET_BASIN',
                                        'estimatedUsers' => 28,
                                    )
                                )
                            )
                        )
                    ),
                    array(
                        'id' => 'cust_marula',
                        'name' => 'Marula Platinum Mine (Pty) Ltd',
                        'code' => 'MPM-099201',
                        'mineManager' => 'Johan Pretorius',
                        'hygienist' => 'Riaan Steyn (SAIOH ROH 0482)',
                        'sites' => array(
                            array(
                                'id' => 'site_marula_shaft',
                                'name' => 'Driekop Shaft & Concentrator',
                                'property' => 'Farm Driekop 253KT, Limpopo',
                                'mineCode' => '099201',
                                'facilities' => array(
                                    array(
                                        'id' => 'fac_marula_male',
                                        'name' => 'Monthly ablution facilities checklist *Male (Shaft 1)',
                                        'type' => 'MALE_ABLUTION',
                                        'estimatedUsers' => 220,
                                    )
                                )
                            )
                        )
                    ),
                    array(
                        'id' => 'cust_phalaborwa',
                        'name' => 'Palabora Copper Mining Company',
                        'code' => 'PMC-004312',
                        'mineManager' => 'T. Ndlovu',
                        'hygienist' => 'Riaan Steyn (SAIOH ROH 0482)',
                        'sites' => array(
                            array(
                                'id' => 'site_pmc_smelter',
                                'name' => 'Smelter & Refinery Division',
                                'property' => 'Copper Road, Phalaborwa',
                                'mineCode' => '004312',
                                'facilities' => array(
                                    array(
                                        'id' => 'fac_pmc_bay3',
                                        'name' => 'Monthly Toilet & Basin *Male (Smelter Bay 3)',
                                        'type' => 'TOILET_BASIN',
                                        'estimatedUsers' => 60,
                                    )
                                )
                            )
                        )
                    )
                ));
            },
            'permission_callback' => '__return_true',
        ));
    }
}

new WFHSA_Location_Selector();
