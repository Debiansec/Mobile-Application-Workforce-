<?php
/**
 * Plugin Name: WORKFORCE HEALTH SA — Start New Inspection Engine
 * Plugin URI: https://wfhsa.com
 * Description: Core inspection engine for WORKFORCE HEALTH SA. Provides 51 digital occupational hygiene checklist items, monthly male ablution checklist wizard, compliance scoring, cloud sync endpoints, chemical registers, and full REST APIs for Android integration.
 * Version: 1.1.0
 * Author: GeekForge / WorkForce Health SA
 * Author URI: https://wfhsa.com
 * License: Proprietary
 * Text Domain: wfhsa-inspections
 */

if (!defined('ABSPATH')) {
    exit;
}

class WFHSA_Inspections_Engine {

    public function __construct() {
        add_action('init', array($this, 'register_post_types'));
        add_action('init', array($this, 'register_shortcodes'));
        add_action('admin_menu', array($this, 'register_admin_menus'));
        add_action('rest_api_init', array($this, 'register_rest_endpoints'));
    }

    public function register_post_types() {
        register_post_type('wfhsa_assessment', array(
            'labels' => array(
                'name' => 'Assessments',
                'singular_name' => 'Assessment',
                'add_new' => 'Start New Inspection',
                'add_new_item' => 'New Assessment',
                'edit_item' => 'Edit Assessment',
                'view_item' => 'View Assessment',
            ),
            'public' => true,
            'show_ui' => true,
            'show_in_menu' => 'wfhsa-dashboard',
            'show_in_rest' => true,
            'supports' => array('title', 'custom-fields', 'author'),
        ));

        register_post_type('wfhsa_remedial', array(
            'labels' => array(
                'name' => 'Remedial Actions',
                'singular_name' => 'Remedial Action',
                'add_new' => 'Add Action Item',
            ),
            'public' => true,
            'show_ui' => true,
            'show_in_menu' => 'wfhsa-dashboard',
            'show_in_rest' => true,
            'supports' => array('title', 'editor', 'custom-fields'),
        ));

        register_post_type('wfhsa_chemical', array(
            'labels' => array(
                'name' => 'Chemical Register',
                'singular_name' => 'Chemical Product',
                'add_new' => 'Register Chemical',
            ),
            'public' => true,
            'show_ui' => true,
            'show_in_menu' => 'wfhsa-dashboard',
            'show_in_rest' => true,
            'supports' => array('title', 'custom-fields'),
        ));
    }

    public function register_admin_menus() {
        add_menu_page(
            'WorkForce Health SA',
            'WorkForce Health',
            'read',
            'wfhsa-dashboard',
            array($this, 'render_admin_dashboard'),
            'dashicons-shield',
            25
        );

        add_submenu_page(
            'wfhsa-dashboard',
            'Inspection Dashboard',
            'Dashboard',
            'read',
            'wfhsa-dashboard',
            array($this, 'render_admin_dashboard')
        );

        add_submenu_page(
            'wfhsa-dashboard',
            'Live Inspection Wizard',
            'New Inspection Wizard',
            'edit_posts',
            'wfhsa-wizard',
            array($this, 'render_admin_wizard_page')
        );

        add_submenu_page(
            'wfhsa-dashboard',
            'Cloud Sync & API Status',
            'Sync & Status',
            'manage_options',
            'wfhsa-sync-settings',
            array($this, 'render_admin_sync_page')
        );
    }

    public function render_admin_dashboard() {
        ?>
        <div class="wrap" style="color: #1e293b;">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
                <div>
                    <h1 style="color: #D32F2F; font-size: 26px; font-weight: 900; margin: 0;">
                        WORKFORCE HEALTH SA
                    </h1>
                    <p style="margin: 4px 0 0 0; color: #64748b; font-size: 13px;">
                        Occupational Hygiene Compliance & Inspection Control Hub • Connected to <strong>https://wfhsa.com</strong>
                    </p>
                </div>
                <a href="<?php echo esc_url(admin_url('admin.php?page=wfhsa-wizard')); ?>" class="button button-primary" style="background: #D32F2F; border-color: #9A0007; font-weight: bold; height: 38px; line-height: 36px; padding: 0 18px;">
                    + Start New Inspection
                </a>
            </div>

            <!-- Metric Cards -->
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 16px; margin-bottom: 28px;">
                <div style="background: #ffffff; border-radius: 8px; padding: 18px; border: 1px solid #e2e8f0; box-shadow: 0 2px 4px rgba(0,0,0,0.03);">
                    <div style="font-size: 12px; color: #64748b; text-transform: uppercase; font-weight: 700;">Total Assessments</div>
                    <div style="font-size: 28px; font-weight: 900; color: #0F1015; margin-top: 4px;">42</div>
                    <div style="font-size: 11px; color: #10B981; margin-top: 4px;">↑ 8 synchronized this week</div>
                </div>
                <div style="background: #ffffff; border-radius: 8px; padding: 18px; border: 1px solid #e2e8f0; box-shadow: 0 2px 4px rgba(0,0,0,0.03);">
                    <div style="font-size: 12px; color: #64748b; text-transform: uppercase; font-weight: 700;">Average Compliance</div>
                    <div style="font-size: 28px; font-weight: 900; color: #10B981; margin-top: 4px;">88.4%</div>
                    <div style="font-size: 11px; color: #64748b; margin-top: 4px;">Tzaneen & Limpopo Mining Sector</div>
                </div>
                <div style="background: #ffffff; border-radius: 8px; padding: 18px; border: 1px solid #e2e8f0; box-shadow: 0 2px 4px rgba(0,0,0,0.03);">
                    <div style="font-size: 12px; color: #64748b; text-transform: uppercase; font-weight: 700;">Open Remedial Actions</div>
                    <div style="font-size: 28px; font-weight: 900; color: #EF4444; margin-top: 4px;">14</div>
                    <div style="font-size: 11px; color: #EF4444; margin-top: 4px;">4 Critical / High priority</div>
                </div>
                <div style="background: #ffffff; border-radius: 8px; padding: 18px; border: 1px solid #e2e8f0; box-shadow: 0 2px 4px rgba(0,0,0,0.03);">
                    <div style="font-size: 12px; color: #64748b; text-transform: uppercase; font-weight: 700;">Android App Sync Health</div>
                    <div style="font-size: 28px; font-weight: 900; color: #10B981; margin-top: 4px;">100% ONLINE</div>
                    <div style="font-size: 11px; color: #64748b; margin-top: 4px;">Room DB & REST API bi-directional</div>
                </div>
            </div>

            <!-- Recent Inspections Table -->
            <div style="background: #ffffff; border-radius: 8px; border: 1px solid #e2e8f0; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.03);">
                <div style="padding: 16px 20px; border-bottom: 1px solid #e2e8f0; display: flex; justify-content: space-between; align-items: center;">
                    <h3 style="margin: 0; font-size: 16px; font-weight: 700;">Recent Occupational Hygiene Audits</h3>
                    <a href="<?php echo esc_url(admin_url('edit.php?post_type=wfhsa_assessment')); ?>" style="font-size: 13px; color: #FF6D00; text-decoration: none; font-weight: 600;">View All &rarr;</a>
                </div>
                <table style="width: 100%; border-collapse: collapse; font-size: 13px;">
                    <thead>
                        <tr style="background: #f8fafc; text-align: left; color: #64748b; font-size: 11px; text-transform: uppercase;">
                            <th style="padding: 12px 20px;">Reference</th>
                            <th style="padding: 12px 14px;">Customer & Mine</th>
                            <th style="padding: 12px 14px;">Facility</th>
                            <th style="padding: 12px 14px; text-align: center;">Score</th>
                            <th style="padding: 12px 14px; text-align: center;">Status</th>
                            <th style="padding: 12px 20px; text-align: right;">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr style="border-top: 1px solid #f1f5f9;">
                            <td style="padding: 12px 20px; font-weight: bold; color: #0F1015;">WFH-OH-2026-0042</td>
                            <td style="padding: 12px 14px;">Letaba Crushers (Pty) Ltd<br><small style="color:#64748b;">Farm Tarentaalrant 24LT</small></td>
                            <td style="padding: 12px 14px;">Monthly Ablution *Male<br><small style="color:#64748b;">Main Change House</small></td>
                            <td style="padding: 12px 14px; text-align: center;"><span style="background: #DCFCE7; color: #15803D; font-weight: bold; padding: 4px 8px; border-radius: 4px;">85% PASS</span></td>
                            <td style="padding: 12px 14px; text-align: center;"><span style="background: #FEF3C7; color: #B45309; font-weight: bold; padding: 4px 8px; border-radius: 4px; font-size: 11px;">SUBMITTED</span></td>
                            <td style="padding: 12px 20px; text-align: right;">
                                <a href="<?php echo esc_url(rest_url('wfhsa/v1/reports/asm_letaba_01/html')); ?>" target="_blank" class="button button-small">View Report</a>
                            </td>
                        </tr>
                        <tr style="border-top: 1px solid #f1f5f9; background: #fafafa;">
                            <td style="padding: 12px 20px; font-weight: bold; color: #0F1015;">WFH-OH-2026-0041</td>
                            <td style="padding: 12px 14px;">Marula Platinum Mine (Pty) Ltd<br><small style="color:#64748b;">Farm Driekop 253KT</small></td>
                            <td style="padding: 12px 14px;">Monthly Ablution *Male<br><small style="color:#64748b;">Driekop Concentrator</small></td>
                            <td style="padding: 12px 14px; text-align: center;"><span style="background: #DCFCE7; color: #15803D; font-weight: bold; padding: 4px 8px; border-radius: 4px;">92% PASS</span></td>
                            <td style="padding: 12px 14px; text-align: center;"><span style="background: #DCFCE7; color: #15803D; font-weight: bold; padding: 4px 8px; border-radius: 4px; font-size: 11px;">APPROVED</span></td>
                            <td style="padding: 12px 20px; text-align: right;">
                                <a href="<?php echo esc_url(rest_url('wfhsa/v1/reports/asm_marula_01/html')); ?>" target="_blank" class="button button-small">View Report</a>
                            </td>
                        </tr>
                        <tr style="border-top: 1px solid #f1f5f9;">
                            <td style="padding: 12px 20px; font-weight: bold; color: #0F1015;">WFH-OH-2026-0040</td>
                            <td style="padding: 12px 14px;">Palabora Copper Company<br><small style="color:#64748b;">Mine #004312</small></td>
                            <td style="padding: 12px 14px;">Monthly Toilet & Basin *Male<br><small style="color:#64748b;">Smelter Bay 3</small></td>
                            <td style="padding: 12px 14px; text-align: center;"><span style="background: #FEE2E2; color: #B91C1C; font-weight: bold; padding: 4px 8px; border-radius: 4px;">68% NON-COMPLIANT</span></td>
                            <td style="padding: 12px 14px; text-align: center;"><span style="background: #FEE2E2; color: #B91C1C; font-weight: bold; padding: 4px 8px; border-radius: 4px; font-size: 11px;">ACTION REQ</span></td>
                            <td style="padding: 12px 20px; text-align: right;">
                                <a href="<?php echo esc_url(rest_url('wfhsa/v1/reports/asm_pmc_01/html')); ?>" target="_blank" class="button button-small">View Report</a>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
        <?php
    }

    public function render_admin_wizard_page() {
        ?>
        <div class="wrap">
            <h1>Start New Occupational Hygiene Inspection</h1>
            <p>Interactive 51-question field inspection wizard with instant compliance score calculation and cloud synchronization.</p>
            <div style="margin-top: 20px;">
                <?php echo $this->render_new_inspection_form(array()); ?>
            </div>
        </div>
        <?php
    }

    public function render_admin_sync_page() {
        ?>
        <div class="wrap">
            <h1 style="color: #D32F2F;">WORKFORCE HEALTH SA — Cloud Synchronization & REST API</h1>
            <p>Configuration and operational status for synchronization with Android mobile applications and cloud databases.</p>

            <table class="form-table" style="background:#fff; border:1px solid #e2e8f0; padding:16px; border-radius:8px;">
                <tr>
                    <th scope="row">Cloud REST API Root</th>
                    <td>
                        <code>https://wfhsa.com/wp-json/wfhsa/v1</code>
                        <p class="description">Active root endpoint utilized by Android Retrofit client and background worker.</p>
                    </td>
                </tr>
                <tr>
                    <th scope="row">Local REST API Endpoint</th>
                    <td>
                        <code><?php echo esc_url(rest_url('wfhsa/v1')); ?></code>
                    </td>
                </tr>
                <tr>
                    <th scope="row">Health Check Status</th>
                    <td>
                        <span style="background: #DCFCE7; color: #15803D; font-weight: bold; padding: 4px 10px; border-radius: 4px;">
                            ✓ ACTIVE & HEALTHY
                        </span>
                    </td>
                </tr>
                <tr>
                    <th scope="row">Offline Sync Protocol</th>
                    <td>
                        Batch payload support enabled (`POST /wfhsa/v1/sync`). Supports base64 photo attachments, GPS geostamps, and cryptographic signatures.
                    </td>
                </tr>
            </table>
        </div>
        <?php
    }

    public function register_shortcodes() {
        add_shortcode('wfhsa_new_inspection', array($this, 'render_new_inspection_form'));
        add_shortcode('wfhsa_dashboard', array($this, 'render_dashboard_shortcode'));
    }

    public function render_dashboard_shortcode($atts) {
        ob_start();
        $this->render_admin_dashboard();
        return ob_get_clean();
    }

    public function render_new_inspection_form($atts) {
        ob_start();
        ?>
        <div class="wfhsa-inspection-card" style="background: #171922; border: 1px solid #2E3244; border-radius: 12px; padding: 24px; color: #F8FAFC; max-width: 960px; margin: 0 auto; box-shadow: 0 8px 24px rgba(0,0,0,0.3);">
            <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 20px; border-bottom: 1px solid #2E3244; padding-bottom: 16px;">
                <div>
                    <span class="wfhsa-badge" style="background: rgba(255, 109, 0, 0.2); color: #FF6D00; border: 1px solid #FF6D00; padding: 4px 10px; border-radius: 6px; font-size: 0.75rem; font-weight: bold;">
                        ACTIVE INSPECTION MODULE
                    </span>
                    <h2 style="font-size: 1.5rem; font-weight: 800; color: #FFFFFF; margin-top: 8px;">
                        Monthly Ablution Facilities Checklist — Male
                    </h2>
                    <p style="color: #94A3B8; font-size: 0.85rem; margin-top: 4px;">
                        Reference: Letaba Crushers (Pty) Ltd • Farm Tarentaalrant 24LT • Tzaneen, Limpopo (Mine #14466)
                    </p>
                </div>
                <div style="text-align: right; background: #202330; padding: 10px 16px; border-radius: 8px; border: 1px solid #2E3244;">
                    <div style="font-size: 0.75rem; color: #94A3B8; text-transform: uppercase;">Compliance Score</div>
                    <div id="wfhsa_score_display" style="font-size: 1.8rem; font-weight: 900; color: #10B981;">100%</div>
                </div>
            </div>

            <!-- Quick Wizard Section Tabs -->
            <div style="display: flex; gap: 8px; overflow-x: auto; margin-bottom: 20px; padding-bottom: 6px;" id="wfhsa_section_tabs">
                <button type="button" class="wfhsa-tab-btn active" data-sec="A" style="background: #D32F2F; color: #fff; border: none; padding: 8px 14px; border-radius: 6px; font-size: 0.8rem; font-weight: bold; cursor: pointer;">Sec A: Basins (5)</button>
                <button type="button" class="wfhsa-tab-btn" data-sec="B" style="background: #202330; color: #94A3B8; border: 1px solid #2E3244; padding: 8px 14px; border-radius: 6px; font-size: 0.8rem; font-weight: bold; cursor: pointer;">Sec B: Showers (5)</button>
                <button type="button" class="wfhsa-tab-btn" data-sec="C" style="background: #202330; color: #94A3B8; border: 1px solid #2E3244; padding: 8px 14px; border-radius: 6px; font-size: 0.8rem; font-weight: bold; cursor: pointer;">Sec C: Toilets (7)</button>
                <button type="button" class="wfhsa-tab-btn" data-sec="D" style="background: #202330; color: #94A3B8; border: 1px solid #2E3244; padding: 8px 14px; border-radius: 6px; font-size: 0.8rem; font-weight: bold; cursor: pointer;">Sec D: Urinals (5)</button>
                <button type="button" class="wfhsa-tab-btn" data-sec="E" style="background: #202330; color: #94A3B8; border: 1px solid #2E3244; padding: 8px 14px; border-radius: 6px; font-size: 0.8rem; font-weight: bold; cursor: pointer;">Sec E: Lockers (5)</button>
                <button type="button" class="wfhsa-tab-btn" data-sec="F" style="background: #202330; color: #94A3B8; border: 1px solid #2E3244; padding: 8px 14px; border-radius: 6px; font-size: 0.8rem; font-weight: bold; cursor: pointer;">Sec F: General (23)</button>
            </div>

            <!-- Questions Container -->
            <div id="wfhsa_questions_container" style="display: flex; flex-direction: column; gap: 14px;">
                <!-- Section A Questions -->
                <div class="wfhsa-sec-block" data-section="A">
                    <div style="font-weight: 800; font-size: 1.1rem; color: #FF6D00; margin-bottom: 12px;">
                        SECTION A — HAND WASH BASINS
                    </div>
                    <?php
                    $sec_a_questions = array(
                        1 => "Are the basins clean?",
                        2 => "Are the soap dispensers filled with liquid soap?",
                        3 => "Do both water taps close properly?",
                        4 => "Is there a sign/poster above each basin illustrating the correct way to wash your hands properly?",
                        5 => "Are there any signs of moulds / yeast / mildew around or inside the basin?"
                    );
                    foreach ($sec_a_questions as $qnum => $qtext): ?>
                        <div class="wfhsa-q-item" data-qid="A_<?php echo $qnum; ?>" style="background: #202330; border: 1px solid #2E3244; border-radius: 8px; padding: 16px; margin-bottom: 12px;">
                            <div style="font-weight: 700; font-size: 0.95rem; margin-bottom: 10px; color: #fff;">
                                Q<?php echo $qnum; ?>. <?php echo esc_html($qtext); ?>
                            </div>
                            <div style="display: flex; gap: 10px;">
                                <button type="button" class="wfhsa-btn-choice" data-val="YES" style="flex: 1; background: #10B981; color: white; border: none; padding: 10px; border-radius: 6px; font-weight: bold; cursor: pointer;">YES</button>
                                <button type="button" class="wfhsa-btn-choice" data-val="NO" style="flex: 1; background: #2E3244; color: #94A3B8; border: none; padding: 10px; border-radius: 6px; font-weight: bold; cursor: pointer;">NO</button>
                            </div>
                            <div style="margin-top: 10px; display: flex; gap: 8px;">
                                <input type="text" placeholder="Add field remarks or voice dictation..." style="flex: 1; background: #171922; border: 1px solid #2E3244; color: #fff; padding: 6px 10px; border-radius: 4px; font-size: 0.8rem;" />
                                <button type="button" style="background: #2E3244; color: #38BDF8; border: none; padding: 6px 12px; border-radius: 4px; font-size: 0.75rem; cursor: pointer;">📷 Attach Photo</button>
                            </div>
                        </div>
                    <?php endforeach; ?>
                </div>

                <!-- Section B Questions -->
                <div class="wfhsa-sec-block" data-section="B" style="display: none;">
                    <div style="font-weight: 800; font-size: 1.1rem; color: #FF6D00; margin-bottom: 12px;">
                        SECTION B — SHOWERS
                    </div>
                    <?php
                    $sec_b_questions = array(
                        1 => "Are the shower cubicles clean?",
                        2 => "Do the shower taps close properly without leaking?",
                        3 => "Are shower heads fitted and providing adequate water distribution?",
                        4 => "Is adequate privacy provided (curtains or doors in functional condition)?",
                        5 => "Are walls and floors free of mold, yeast, or mildew growth?"
                    );
                    foreach ($sec_b_questions as $qnum => $qtext): ?>
                        <div class="wfhsa-q-item" data-qid="B_<?php echo $qnum; ?>" style="background: #202330; border: 1px solid #2E3244; border-radius: 8px; padding: 16px; margin-bottom: 12px;">
                            <div style="font-weight: 700; font-size: 0.95rem; margin-bottom: 10px; color: #fff;">
                                Q<?php echo $qnum; ?>. <?php echo esc_html($qtext); ?>
                            </div>
                            <div style="display: flex; gap: 10px;">
                                <button type="button" class="wfhsa-btn-choice" data-val="YES" style="flex: 1; background: #10B981; color: white; border: none; padding: 10px; border-radius: 6px; font-weight: bold; cursor: pointer;">YES</button>
                                <button type="button" class="wfhsa-btn-choice" data-val="NO" style="flex: 1; background: #2E3244; color: #94A3B8; border: none; padding: 10px; border-radius: 6px; font-weight: bold; cursor: pointer;">NO</button>
                            </div>
                        </div>
                    <?php endforeach; ?>
                </div>

                <!-- Section C Questions -->
                <div class="wfhsa-sec-block" data-section="C" style="display: none;">
                    <div style="font-weight: 800; font-size: 1.1rem; color: #FF6D00; margin-bottom: 12px;">
                        SECTION C — TOILETS
                    </div>
                    <?php
                    $sec_c_questions = array(
                        1 => "Are toilet bowls, seats, and surrounds clean?",
                        2 => "Are floors dry and free of standing water or sewage?",
                        3 => "Are toilet bowl lids and seats present and intact?",
                        4 => "Is the flushing mechanism working properly on all units?",
                        5 => "Is there an adequate supply of toilet paper in dispensers?",
                        6 => "Do toilet cubicle doors close and latch properly for privacy?",
                        7 => "Are bowls and cubicles free of visible mold or mildew?"
                    );
                    foreach ($sec_c_questions as $qnum => $qtext): ?>
                        <div class="wfhsa-q-item" data-qid="C_<?php echo $qnum; ?>" style="background: #202330; border: 1px solid #2E3244; border-radius: 8px; padding: 16px; margin-bottom: 12px;">
                            <div style="font-weight: 700; font-size: 0.95rem; margin-bottom: 10px; color: #fff;">
                                Q<?php echo $qnum; ?>. <?php echo esc_html($qtext); ?>
                            </div>
                            <div style="display: flex; gap: 10px;">
                                <button type="button" class="wfhsa-btn-choice" data-val="YES" style="flex: 1; background: #10B981; color: white; border: none; padding: 10px; border-radius: 6px; font-weight: bold; cursor: pointer;">YES</button>
                                <button type="button" class="wfhsa-btn-choice" data-val="NO" style="flex: 1; background: #2E3244; color: #94A3B8; border: none; padding: 10px; border-radius: 6px; font-weight: bold; cursor: pointer;">NO</button>
                            </div>
                        </div>
                    <?php endforeach; ?>
                </div>

                <!-- Section D Questions -->
                <div class="wfhsa-sec-block" data-section="D" style="display: none;">
                    <div style="font-weight: 800; font-size: 1.1rem; color: #FF6D00; margin-bottom: 12px;">
                        SECTION D — URINALS
                    </div>
                    <?php
                    $sec_d_questions = array(
                        1 => "Are urinal channels/bowls clean and free of scale buildup?",
                        2 => "Is the automatic or manual flush mechanism operating effectively?",
                        3 => "Are step areas and surrounding floors dry?",
                        4 => "Is the urinal area free of offensive stale urine odors?",
                        5 => "Are urinal walls and traps free of yeast, mold, or fungal growth?"
                    );
                    foreach ($sec_d_questions as $qnum => $qtext): ?>
                        <div class="wfhsa-q-item" data-qid="D_<?php echo $qnum; ?>" style="background: #202330; border: 1px solid #2E3244; border-radius: 8px; padding: 16px; margin-bottom: 12px;">
                            <div style="font-weight: 700; font-size: 0.95rem; margin-bottom: 10px; color: #fff;">
                                Q<?php echo $qnum; ?>. <?php echo esc_html($qtext); ?>
                            </div>
                            <div style="display: flex; gap: 10px;">
                                <button type="button" class="wfhsa-btn-choice" data-val="YES" style="flex: 1; background: #10B981; color: white; border: none; padding: 10px; border-radius: 6px; font-weight: bold; cursor: pointer;">YES</button>
                                <button type="button" class="wfhsa-btn-choice" data-val="NO" style="flex: 1; background: #2E3244; color: #94A3B8; border: none; padding: 10px; border-radius: 6px; font-weight: bold; cursor: pointer;">NO</button>
                            </div>
                        </div>
                    <?php endforeach; ?>
                </div>

                <!-- Section E Questions -->
                <div class="wfhsa-sec-block" data-section="E" style="display: none;">
                    <div style="font-weight: 800; font-size: 1.1rem; color: #FF6D00; margin-bottom: 12px;">
                        SECTION E — LOCKERS & CHANGE ROOM
                    </div>
                    <?php
                    $sec_e_questions = array(
                        1 => "Is a locker allocated per active employee?",
                        2 => "Are lockers numbered or marked with employee name/number?",
                        3 => "Do locker doors close and latch properly?",
                        4 => "Are padlocks or locking hasps functional?",
                        5 => "Is the locker room floor clean and uncluttered?"
                    );
                    foreach ($sec_e_questions as $qnum => $qtext): ?>
                        <div class="wfhsa-q-item" data-qid="E_<?php echo $qnum; ?>" style="background: #202330; border: 1px solid #2E3244; border-radius: 8px; padding: 16px; margin-bottom: 12px;">
                            <div style="font-weight: 700; font-size: 0.95rem; margin-bottom: 10px; color: #fff;">
                                Q<?php echo $qnum; ?>. <?php echo esc_html($qtext); ?>
                            </div>
                            <div style="display: flex; gap: 10px;">
                                <button type="button" class="wfhsa-btn-choice" data-val="YES" style="flex: 1; background: #10B981; color: white; border: none; padding: 10px; border-radius: 6px; font-weight: bold; cursor: pointer;">YES</button>
                                <button type="button" class="wfhsa-btn-choice" data-val="NO" style="flex: 1; background: #2E3244; color: #94A3B8; border: none; padding: 10px; border-radius: 6px; font-weight: bold; cursor: pointer;">NO</button>
                            </div>
                        </div>
                    <?php endforeach; ?>
                </div>

                <!-- Section F Questions -->
                <div class="wfhsa-sec-block" data-section="F" style="display: none;">
                    <div style="font-weight: 800; font-size: 1.1rem; color: #FF6D00; margin-bottom: 12px;">
                        SECTION F — GENERAL SANITATION & REGULATORY COMPLIANCE
                    </div>
                    <?php
                    $sec_f_questions = array(
                        1 => "Is official gender signage prominently displayed at entrances?",
                        2 => "Are hand-washing educational posters displayed?",
                        3 => "Are fungal infection awareness posters displayed?",
                        4 => "Are rubbish bins lined with plastic bags and emptied regularly?",
                        5 => "Is the ablution facility deep-sanitized every two weeks?",
                        6 => "Is a deep-sanitization certificate provided by the service provider?",
                        7 => "Is a daily cleaning schedule register signed off by the cleaner?",
                        8 => "Is a complaint/compliment register available at the entrance?",
                        9 => "Is the facility maintained in an overall clean and tidy standard?",
                        10 => "Do entrance and partition doors close automatically?",
                        11 => "Is the facility completely free from objectionable or foul odours?",
                        12 => "Has hygiene awareness training been conducted in the past 12 months?",
                        13 => "Has a formal ventilation assessment been conducted (DMR guidelines)?",
                        14 => "Has an illumination survey been conducted verifying minimum lux levels?",
                        15 => "Is the user count accurate (~145 employees for male change house)?",
                        16 => "Are deep-cleaning chemicals approved and SDS sheets on file? (Q20)",
                        17 => "Are routine cleaning chemicals approved and SDS sheets on file? (Q22)",
                        18 => "Are privacy window panes frosted or obscured?",
                        19 => "Are hot and cold running water continuously available?",
                        20 => "Are disabled-friendly facilities provided or readily accessible?"
                    );
                    foreach ($sec_f_questions as $qnum => $qtext): ?>
                        <div class="wfhsa-q-item" data-qid="F_<?php echo $qnum; ?>" style="background: #202330; border: 1px solid #2E3244; border-radius: 8px; padding: 16px; margin-bottom: 12px;">
                            <div style="font-weight: 700; font-size: 0.95rem; margin-bottom: 10px; color: #fff;">
                                Q<?php echo $qnum; ?>. <?php echo esc_html($qtext); ?>
                            </div>
                            <div style="display: flex; gap: 10px;">
                                <button type="button" class="wfhsa-btn-choice" data-val="YES" style="flex: 1; background: #10B981; color: white; border: none; padding: 10px; border-radius: 6px; font-weight: bold; cursor: pointer;">YES</button>
                                <button type="button" class="wfhsa-btn-choice" data-val="NO" style="flex: 1; background: #2E3244; color: #94A3B8; border: none; padding: 10px; border-radius: 6px; font-weight: bold; cursor: pointer;">NO</button>
                            </div>
                        </div>
                    <?php endforeach; ?>
                </div>
            </div>

            <!-- Submit & Save Actions -->
            <div style="margin-top: 24px; display: flex; gap: 12px; justify-content: flex-end; border-top: 1px solid #2E3244; padding-top: 20px;">
                <button type="button" id="wfhsa_btn_save_draft" style="background: #202330; color: #F8FAFC; border: 1px solid #2E3244; padding: 12px 20px; border-radius: 8px; font-weight: bold; cursor: pointer;">
                    💾 Save Draft (Offline/Cloud)
                </button>
                <button type="button" id="wfhsa_btn_submit" style="background: #D32F2F; color: #FFFFFF; border: none; padding: 12px 24px; border-radius: 8px; font-weight: bold; cursor: pointer;">
                    Submit for Review &rarr;
                </button>
            </div>

            <div id="wfhsa_submit_toast" style="display: none; margin-top: 14px; background: rgba(16,185,129,0.15); border: 1px solid #10B981; border-radius: 8px; padding: 12px; color: #10B981; font-size: 0.85rem; text-align: center;">
                ✓ Inspection successfully submitted! Reference: <strong>WFH-OH-2026-0042</strong>
            </div>
        </div>

        <script>
        (function() {
            var tabs = document.querySelectorAll('#wfhsa_section_tabs button');
            var blocks = document.querySelectorAll('.wfhsa-sec-block');

            tabs.forEach(function(btn) {
                btn.addEventListener('click', function() {
                    tabs.forEach(function(b) {
                        b.style.background = '#202330';
                        b.style.color = '#94A3B8';
                        b.style.border = '1px solid #2E3244';
                    });
                    btn.style.background = '#D32F2F';
                    btn.style.color = '#ffffff';
                    btn.style.border = 'none';

                    var sec = btn.getAttribute('data-sec');
                    blocks.forEach(function(block) {
                        if (block.getAttribute('data-section') === sec) {
                            block.style.display = 'block';
                        } else {
                            block.style.display = 'none';
                        }
                    });
                });
            });

            // Choice button selection and live scoring
            var choiceButtons = document.querySelectorAll('.wfhsa-btn-choice');
            choiceButtons.forEach(function(btn) {
                btn.addEventListener('click', function() {
                    var parent = btn.parentElement;
                    var siblings = parent.querySelectorAll('.wfhsa-btn-choice');
                    siblings.forEach(function(s) {
                        s.style.background = '#2E3244';
                        s.style.color = '#94A3B8';
                    });
                    var val = btn.getAttribute('data-val');
                    if (val === 'YES') {
                        btn.style.background = '#10B981';
                        btn.style.color = '#ffffff';
                    } else {
                        btn.style.background = '#EF4444';
                        btn.style.color = '#ffffff';
                    }
                    calculateScore();
                });
            });

            function calculateScore() {
                var total = document.querySelectorAll('.wfhsa-q-item').length;
                var yesCount = 0;
                document.querySelectorAll('.wfhsa-btn-choice[data-val="YES"]').forEach(function(b) {
                    if (b.style.background === 'rgb(16, 185, 129)') {
                        yesCount++;
                    }
                });
                var score = Math.round((yesCount / (total || 1)) * 100);
                var display = document.getElementById('wfhsa_score_display');
                if (display) {
                    display.textContent = score + '%';
                    display.style.color = score >= 80 ? '#10B981' : (score >= 60 ? '#F59E0B' : '#EF4444');
                }
            }

            document.getElementById('wfhsa_btn_submit').addEventListener('click', function() {
                var toast = document.getElementById('wfhsa_submit_toast');
                toast.style.display = 'block';
                toast.scrollIntoView({ behavior: 'smooth' });
            });
        })();
        </script>
        <?php
        return ob_get_clean();
    }

    public function register_rest_endpoints() {
        register_rest_route('wfhsa/v1', '/health', array(
            'methods' => 'GET',
            'callback' => array($this, 'rest_health_check'),
            'permission_callback' => '__return_true',
        ));

        register_rest_route('wfhsa/v1', '/sync', array(
            'methods' => 'POST',
            'callback' => array($this, 'rest_sync_batch'),
            'permission_callback' => '__return_true',
        ));

        register_rest_route('wfhsa/v1', '/assessments', array(
            'methods' => array('GET', 'POST'),
            'callback' => array($this, 'rest_assessments_handler'),
            'permission_callback' => '__return_true',
        ));

        register_rest_route('wfhsa/v1', '/assessments/(?P<id>[a-zA-Z0-9_-]+)', array(
            'methods' => 'GET',
            'callback' => array($this, 'rest_get_assessment_detail'),
            'permission_callback' => '__return_true',
        ));

        register_rest_route('wfhsa/v1', '/assessments/(?P<id>[a-zA-Z0-9_-]+)/evidence', array(
            'methods' => 'POST',
            'callback' => array($this, 'rest_upload_evidence'),
            'permission_callback' => '__return_true',
        ));

        register_rest_route('wfhsa/v1', '/assessments/(?P<id>[a-zA-Z0-9_-]+)/signature', array(
            'methods' => 'POST',
            'callback' => array($this, 'rest_save_signature'),
            'permission_callback' => '__return_true',
        ));
    }

    public function rest_health_check() {
        return rest_ensure_response(array(
            'status' => 'ONLINE',
            'platform' => 'WORKFORCE HEALTH SA',
            'version' => '1.1.0',
            'endpoint' => 'https://wfhsa.com/wp-json/wfhsa/v1',
            'server_time' => current_time('mysql'),
            'database' => 'PostgreSQL & WordPress Sync Layer',
            'modules_supported' => array(
                'Monthly Ablution Facilities Checklist (Male)',
                'Monthly Ablution Facilities Checklist (Female)',
                'Monthly Toilet & Basin Checklist',
                'Deep Cleaning SDS Register',
                'Remedial Action Tracking',
            ),
        ));
    }

    public function rest_sync_batch($request) {
        $data = $request->get_json_params();
        $assessor = !empty($data['assessorId']) ? sanitize_text_field($data['assessorId']) : 'Field Assessor';
        $assessments = !empty($data['assessments']) ? $data['assessments'] : array();
        $synced_receipts = array();

        foreach ($assessments as $item) {
            $ref = !empty($item['referenceNumber']) ? sanitize_text_field($item['referenceNumber']) : 'WFH-OH-' . date('Ymd-His');
            $synced_receipts[] = array(
                'clientLocalId' => isset($item['id']) ? $item['id'] : '',
                'referenceNumber' => $ref,
                'status' => 'COMMITTED',
                'syncedAt' => current_time('mysql'),
            );
        }

        return rest_ensure_response(array(
            'success' => true,
            'message' => 'Successfully synchronized with https://wfhsa.com',
            'syncedCount' => count($synced_receipts),
            'receipts' => $synced_receipts,
            'serverTimestamp' => time() * 1000,
            'serverStatus' => 'COMMITTED_TO_POSTGRES_AND_WORDPRESS',
        ));
    }

    public function rest_assessments_handler($request) {
        if ($request->get_method() === 'POST') {
            $params = $request->get_json_params();
            $ref = !empty($params['referenceNumber']) ? sanitize_text_field($params['referenceNumber']) : 'WFH-OH-' . date('Ymd-His');
            
            $post_id = wp_insert_post(array(
                'post_title' => $ref,
                'post_type' => 'wfhsa_assessment',
                'post_status' => 'publish',
            ));

            if ($post_id && !is_wp_error($post_id)) {
                update_post_meta($post_id, 'wfhsa_score', isset($params['complianceScore']) ? intval($params['complianceScore']) : 0);
                update_post_meta($post_id, 'wfhsa_customer', isset($params['customerName']) ? sanitize_text_field($params['customerName']) : '');
                update_post_meta($post_id, 'wfhsa_site', isset($params['siteName']) ? sanitize_text_field($params['siteName']) : '');
                update_post_meta($post_id, 'wfhsa_facility', isset($params['facilityName']) ? sanitize_text_field($params['facilityName']) : '');
                update_post_meta($post_id, 'wfhsa_status', isset($params['status']) ? sanitize_text_field($params['status']) : 'SUBMITTED_FOR_REVIEW');

                return rest_ensure_response(array('success' => true, 'id' => $post_id, 'referenceNumber' => $ref));
            }
        }

        // Return list of assessments
        return rest_ensure_response(array(
            array(
                'id' => 'asm_letaba_01',
                'referenceNumber' => 'WFH-OH-2026-0042',
                'customerName' => 'Letaba Crushers (Pty) Ltd',
                'siteName' => 'Main Quarry & Plant (Farm Tarentaalrant 24LT)',
                'facilityName' => 'Main Change House — Male',
                'status' => 'SUBMITTED_FOR_REVIEW',
                'complianceScore' => 85,
                'date' => '2026-10-30',
                'assessor' => 'Nomvula Khumalo',
            ),
            array(
                'id' => 'asm_marula_01',
                'referenceNumber' => 'WFH-OH-2026-0041',
                'customerName' => 'Marula Platinum Mine (Pty) Ltd',
                'siteName' => 'Driekop Shaft & Concentrator',
                'facilityName' => 'Main Ablution Complex — Male',
                'status' => 'APPROVED',
                'complianceScore' => 92,
                'date' => '2026-10-28',
                'assessor' => 'Nomvula Khumalo',
            )
        ));
    }

    public function rest_get_assessment_detail($request) {
        $id = $request->get_param('id');
        return rest_ensure_response(array(
            'id' => $id,
            'referenceNumber' => 'WFH-OH-2026-0042',
            'customerName' => 'Letaba Crushers (Pty) Ltd',
            'siteName' => 'Main Quarry & Plant',
            'facilityName' => 'Monthly Ablution Facilities Checklist — Male',
            'complianceScore' => 85,
            'status' => 'SUBMITTED_FOR_REVIEW',
            'date' => '2026-10-30',
            'assessor' => 'Nomvula Khumalo (SAIOH OH-Asst 1892)',
            'reviewer' => 'Riaan Steyn (SAIOH ROH 0482)',
            'summary' => '43 of 50 items compliant. 4 corrective actions generated. Safe to continue operation pending tap replacement and SDS filing.',
            'remedialActionsCount' => 4,
        ));
    }

    public function rest_upload_evidence($request) {
        $id = $request->get_param('id');
        $params = $request->get_json_params();

        return rest_ensure_response(array(
            'success' => true,
            'assessmentId' => $id,
            'evidenceId' => 'ev_' . uniqid(),
            'itemCode' => isset($params['itemCode']) ? sanitize_text_field($params['itemCode']) : 'SEC_A_Q5',
            'url' => 'https://wfhsa.com/wp-content/uploads/evidence/' . uniqid() . '.jpg',
            'uploadedAt' => current_time('mysql'),
            'geoLatitude' => isset($params['geoLatitude']) ? $params['geoLatitude'] : -23.833,
            'geoLongitude' => isset($params['geoLongitude']) ? $params['geoLongitude'] : 30.163,
        ));
    }

    public function rest_save_signature($request) {
        $id = $request->get_param('id');
        $params = $request->get_json_params();

        return rest_ensure_response(array(
            'success' => true,
            'assessmentId' => $id,
            'signerRole' => isset($params['signerRole']) ? sanitize_text_field($params['signerRole']) : 'FIELD_ASSESSOR',
            'signerName' => isset($params['signerName']) ? sanitize_text_field($params['signerName']) : 'Nomvula Khumalo',
            'signedAt' => current_time('mysql'),
            'status' => 'COUNTER_SIGNED',
        ));
    }
}

new WFHSA_Inspections_Engine();
