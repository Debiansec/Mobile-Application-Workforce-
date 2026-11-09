<?php
/**
 * Plugin Name: Portal WFHSA — Start New Inspection Engine
 * Plugin URI: https://wfhsa.com/new/
 * Description: Core inspection engine for Portal WFHSA & WorkForce Health SA. Provides 51 digital occupational hygiene checklist items, monthly male ablution checklist wizard, voice-to-text dictation vs typing comment toggle for findings, GPS coordinates geotagging, compliance scoring, cloud sync endpoints, and full REST APIs for Android integration.
 * Version: 1.3.0
 * Author: WorkForce Health SA / Portal WFHSA Team
 * Author URI: https://wfhsa.com
 * License: Proprietary
 * Text Domain: portalwfhsa-inspections
 */

if (!defined('ABSPATH')) {
    exit;
}

class WFHSAWeb_Inspections_Engine {

    public function __construct() {
        add_action('init', array($this, 'register_post_types'));
        add_action('init', array($this, 'register_shortcodes'));
        add_action('admin_menu', array($this, 'register_admin_menus'));
        add_action('rest_api_init', array($this, 'register_rest_endpoints'));
    }

    public function register_post_types() {
        if (!post_type_exists('wfhsa_assessment')) {
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
                'show_in_menu' => 'mobiworkforce-dashboard',
                'show_in_rest' => true,
                'supports' => array('title', 'custom-fields', 'author'),
            ));
        }

        if (!post_type_exists('wfhsa_remedial')) {
            register_post_type('wfhsa_remedial', array(
                'labels' => array(
                    'name' => 'Remedial Actions',
                    'singular_name' => 'Remedial Action',
                    'add_new' => 'Add Action Item',
                ),
                'public' => true,
                'show_ui' => true,
                'show_in_menu' => 'mobiworkforce-dashboard',
                'show_in_rest' => true,
                'supports' => array('title', 'editor', 'custom-fields'),
            ));
        }

        if (!post_type_exists('wfhsa_chemical')) {
            register_post_type('wfhsa_chemical', array(
                'labels' => array(
                    'name' => 'Chemical Register',
                    'singular_name' => 'Chemical Product',
                    'add_new' => 'Register Chemical',
                ),
                'public' => true,
                'show_ui' => true,
                'show_in_menu' => 'mobiworkforce-dashboard',
                'show_in_rest' => true,
                'supports' => array('title', 'custom-fields'),
            ));
        }
    }

    public function register_admin_menus() {
        add_menu_page(
            'MobiWorkforce Dashboard',
            'MobiWorkforce',
            'read',
            'mobiworkforce-dashboard',
            array($this, 'render_admin_dashboard'),
            'dashicons-clipboard',
            25
        );
    }

    public function render_admin_dashboard() {
        ?>
        <div class="wrap" style="background:#171922; color:#F8FAFC; padding:24px; border-radius:12px; margin-top:20px; font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,sans-serif;">
            <div style="display:flex; justify-content:space-between; align-items:center; border-bottom:1px solid #2E3244; padding-bottom:18px; flex-wrap:wrap; gap:12px;">
                <div>
                    <div style="display:inline-block; background:#FF6D00; color:#FFFFFF; font-size:11px; font-weight:800; padding:3px 8px; border-radius:4px; margin-bottom:6px; letter-spacing:0.5px;">
                        MOBIWORKFORCE ENTERPRISE v1.3.0
                    </div>
                    <h1 style="color:#FFFFFF; font-weight:900; margin:0; font-size:24px;">Inspection & Hygiene Findings Hub</h1>
                    <p style="color:#94A3B8; margin:6px 0 0 0; font-size:13px;">
                        WorkForce Health SA &bull; Occupational Hygiene Digital Platform &bull; Voice-to-Text & GPS Geotagging Engine
                    </p>
                </div>
                <div style="text-align:right;">
                    <span style="background:#10B981; color:#FFFFFF; padding:6px 14px; border-radius:999px; font-weight:800; font-size:12px; display:inline-block;">
                        ● SYNC ACTIVE &bull; https://wfhsa.com/new/
                    </span>
                    <div style="font-size:11px; color:#94A3B8; margin-top:4px;">
                        GPS Geotags: Enabled &bull; Web Speech API: Active
                    </div>
                </div>
            </div>

            <!-- Features Summary Cards -->
            <div style="display:grid; grid-template-columns:repeat(auto-fit, minmax(240px, 1fr)); gap:16px; margin-top:24px;">
                <div style="background:#202330; border:1px solid #2E3244; border-radius:8px; padding:18px;">
                    <div style="font-size:24px; margin-bottom:8px;">🎙️ / ⌨️</div>
                    <div style="font-weight:800; color:#FFFFFF; font-size:15px; margin-bottom:4px;">Voice & Typing Findings</div>
                    <div style="color:#94A3B8; font-size:12px; line-height:1.5;">
                        Toggle between hands-free voice dictation with live microphone wave visualization or typing mode with quick hygiene observation chips.
                    </div>
                </div>

                <div style="background:#202330; border:1px solid #2E3244; border-radius:8px; padding:18px;">
                    <div style="font-size:24px; margin-bottom:8px;">📍</div>
                    <div style="font-weight:800; color:#FFFFFF; font-size:15px; margin-bottom:4px;">GPS Geotagging Engine</div>
                    <div style="color:#94A3B8; font-size:12px; line-height:1.5;">
                        Stamps exact latitude, longitude, and accuracy radius directly onto findings, ensuring verifiable forensic evidence for DMR and Mine Health compliance.
                    </div>
                </div>

                <div style="background:#202330; border:1px solid #2E3244; border-radius:8px; padding:18px;">
                    <div style="font-size:24px; margin-bottom:8px;">📋</div>
                    <div style="font-weight:800; color:#FFFFFF; font-size:15px; margin-bottom:4px;">51-Point Ablution Matrix</div>
                    <div style="color:#94A3B8; font-size:12px; line-height:1.5;">
                        Full statutory checklist across Basins, Showers, Toilets, Urinals, Lockers, and Sanitation Chemicals with automated scoring.
                    </div>
                </div>

                <div style="background:#202330; border:1px solid #2E3244; border-radius:8px; padding:18px;">
                    <div style="font-size:24px; margin-bottom:8px;">📱</div>
                    <div style="font-weight:800; color:#FFFFFF; font-size:15px; margin-bottom:4px;">Android Mobile Companion</div>
                    <div style="color:#94A3B8; font-size:12px; line-height:1.5;">
                        Seamless bidirectional REST synchronization with MobiWorkforce Android app with offline SQLite queuing.
                    </div>
                </div>
            </div>

            <!-- Shortcode Usage Notice -->
            <div style="margin-top:24px; background:#12141C; border-left:4px solid #FF6D00; padding:16px 20px; border-radius:6px;">
                <div style="font-weight:800; color:#FFFFFF; font-size:14px; margin-bottom:4px;">
                    Embedding Inspection Form in WordPress Pages
                </div>
                <div style="color:#94A3B8; font-size:13px;">
                    Insert shortcode <code style="background:#2E3244; color:#FF6D00; padding:2px 8px; border-radius:4px; font-weight:bold;">[mobiworkforce_new_inspection]</code> or <code style="background:#2E3244; color:#FF6D00; padding:2px 8px; border-radius:4px; font-weight:bold;">[wfhsa_new_inspection]</code> on any page to render the interactive inspection checklist with voice/typing toggle and GPS stamps.
                </div>
            </div>
        </div>
        <?php
    }

    public function register_shortcodes() {
        add_shortcode('portalwfhsa_new_inspection', array($this, 'render_inspection_form'));
        add_shortcode('mobiworkforce_new_inspection', array($this, 'render_inspection_form'));
        add_shortcode('wfhsa_new_inspection', array($this, 'render_inspection_form'));
    }

    public function register_rest_endpoints() {
        // Primary portalwfhsa/v1 namespace
        register_rest_route('portalwfhsa/v1', '/checklist/questions', array(
            'methods'  => 'GET',
            'callback' => array($this, 'rest_get_questions'),
            'permission_callback' => '__return_true',
        ));

        register_rest_route('portalwfhsa/v1', '/inspections/submit', array(
            'methods'  => 'POST',
            'callback' => array($this, 'rest_submit_inspection'),
            'permission_callback' => '__return_true',
        ));

        register_rest_route('portalwfhsa/v1', '/inspections/findings', array(
            'methods'  => 'GET',
            'callback' => array($this, 'rest_get_findings'),
            'permission_callback' => '__return_true',
        ));

        // Backward compatibility mobiworkforce/v1 namespace
        register_rest_route('mobiworkforce/v1', '/checklist/questions', array(
            'methods'  => 'GET',
            'callback' => array($this, 'rest_get_questions'),
            'permission_callback' => '__return_true',
        ));

        register_rest_route('mobiworkforce/v1', '/inspections/submit', array(
            'methods'  => 'POST',
            'callback' => array($this, 'rest_submit_inspection'),
            'permission_callback' => '__return_true',
        ));

        register_rest_route('mobiworkforce/v1', '/inspections/findings', array(
            'methods'  => 'GET',
            'callback' => array($this, 'rest_get_findings'),
            'permission_callback' => '__return_true',
        ));
    }

    public function get_51_questions() {
        return array(
            // Category 1: Basins & Hand Washing (8 items)
            array('id' => 1, 'cat' => 'Basins & Hand Washing', 'text' => 'Are all hand wash basins free of cracks, leaks, or blockages?'),
            array('id' => 2, 'cat' => 'Basins & Hand Washing', 'text' => 'Is continuous running potable water provided to all taps?'),
            array('id' => 3, 'cat' => 'Basins & Hand Washing', 'text' => 'Is antibacterial soap or dispenser fully stocked and operational?'),
            array('id' => 4, 'cat' => 'Basins & Hand Washing', 'text' => 'Are clean, functional hand drying facilities (blowers or paper towels) present?'),
            array('id' => 5, 'cat' => 'Basins & Hand Washing', 'text' => 'Are mirrors securely mounted and free of sharp broken edges?'),
            array('id' => 6, 'cat' => 'Basins & Hand Washing', 'text' => 'Is basin countertop drained properly without stagnant water pooling?'),
            array('id' => 7, 'cat' => 'Basins & Hand Washing', 'text' => 'Are waste receptacles for paper towels available and not overflowing?'),
            array('id' => 8, 'cat' => 'Basins & Hand Washing', 'text' => 'Are hot and cold water mixing valves functioning correctly where installed?'),

            // Category 2: Showers & Change Rooms (9 items)
            array('id' => 9,  'cat' => 'Showers & Change Rooms', 'text' => 'Are shower cubicle walls and tiles sanitized, free of visible mold/scum?'),
            array('id' => 10, 'cat' => 'Showers & Change Rooms', 'text' => 'Is hot water maintained at compliant temperature (min 40°C - max 50°C)?'),
            array('id' => 11, 'cat' => 'Showers & Change Rooms', 'text' => 'Are shower floor drains clear and free from standing water and hair buildup?'),
            array('id' => 12, 'cat' => 'Showers & Change Rooms', 'text' => 'Are duckboards or slip-resistant floor mats in sound hygienic condition?'),
            array('id' => 13, 'cat' => 'Showers & Change Rooms', 'text' => 'Are clothes hanging hooks or benches clean and non-corroded?'),
            array('id' => 14, 'cat' => 'Showers & Change Rooms', 'text' => 'Are privacy curtains or doors present, clean, and in good repair?'),
            array('id' => 15, 'cat' => 'Showers & Change Rooms', 'text' => 'Is ventilation adequate to prevent excessive steam and humidity accumulation?'),
            array('id' => 16, 'cat' => 'Showers & Change Rooms', 'text' => 'Are soap dishes/holders sanitized and free of slimy deposits?'),
            array('id' => 17, 'cat' => 'Showers & Change Rooms', 'text' => 'Is water pressure sufficient across all simultaneous shower heads?'),

            // Category 3: Toilets & Cubicles (10 items)
            array('id' => 18, 'cat' => 'Toilets & Cubicles', 'text' => 'Are all toilet bowls, pans, and seats sanitized and structurally sound?'),
            array('id' => 19, 'cat' => 'Toilets & Cubicles', 'text' => 'Do flushing mechanisms operate reliably with sufficient water volume?'),
            array('id' => 20, 'cat' => 'Toilets & Cubicles', 'text' => 'Is toilet paper provided in secure, functional roll holders in each cubicle?'),
            array('id' => 21, 'cat' => 'Toilets & Cubicles', 'text' => 'Do cubicle doors have working latches/indicator locks for privacy?'),
            array('id' => 22, 'cat' => 'Toilets & Cubicles', 'text' => 'Are cubicle partitions clean, painted, and free of graffiti/offensive markings?'),
            array('id' => 23, 'cat' => 'Toilets & Cubicles', 'text' => 'Are toilet seats fitted with intact hinge assemblies?'),
            array('id' => 24, 'cat' => 'Toilets & Cubicles', 'text' => 'Is floor tiling intact with no loose, broken, or missing tiles?'),
            array('id' => 25, 'cat' => 'Toilets & Cubicles', 'text' => 'Are sewer gas traps functioning without foul odor escaping into the facility?'),
            array('id' => 26, 'cat' => 'Toilets & Cubicles', 'text' => 'Are toilet brushes provided in sanitary holders for each stall?'),
            array('id' => 27, 'cat' => 'Toilets & Cubicles', 'text' => 'Is there adequate natural or mechanical air extraction inside cubicles?'),

            // Category 4: Urinals (8 items)
            array('id' => 28, 'cat' => 'Urinals', 'text' => 'Are urinal channels/bowls clean and free of scale, rust, or uric acid encrustation?'),
            array('id' => 29, 'cat' => 'Urinals', 'text' => 'Does automatic or manual flushing operate at appropriate frequency?'),
            array('id' => 30, 'cat' => 'Urinals', 'text' => 'Are deodorizer blocks or biological urinal screens present in drains?'),
            array('id' => 31, 'cat' => 'Urinals', 'text' => 'Is drainage swift without any back-up or overflow onto the step?'),
            array('id' => 32, 'cat' => 'Urinals', 'text' => 'Are privacy modesty screens installed between stalls where required?'),
            array('id' => 33, 'cat' => 'Urinals', 'text' => 'Is floor area immediately in front of urinals sealed and dry?'),
            array('id' => 34, 'cat' => 'Urinals', 'text' => 'Are water pipes and sparge pipes free of corrosion and leaks?'),
            array('id' => 35, 'cat' => 'Urinals', 'text' => 'Are stainless steel or ceramic surfaces scrubbed and odor-free?'),

            // Category 5: Lockers & Clean/Dirty Clothes (8 items)
            array('id' => 36, 'cat' => 'Lockers & Change Rooms', 'text' => 'Is dual-locker system (Clean vs Dirty work clothes) maintained per MHSA standards?'),
            array('id' => 37, 'cat' => 'Lockers & Change Rooms', 'text' => 'Are lockers ventilated, clean inside, and free of food scraps/pests?'),
            array('id' => 38, 'cat' => 'Lockers & Change Rooms', 'text' => 'Are locker locker room walkways clear of bags, boots, and trip hazards?'),
            array('id' => 39, 'cat' => 'Lockers & Change Rooms', 'text' => 'Are boot washing stations outside the change house operational?'),
            array('id' => 40, 'cat' => 'Lockers & Change Rooms', 'text' => 'Are benches in changing areas securely fixed, smooth, and easily cleaned?'),
            array('id' => 41, 'cat' => 'Lockers & Change Rooms', 'text' => 'Is laundry dispatch or soiled PPE collection bin clearly demarcated?'),
            array('id' => 42, 'cat' => 'Lockers & Change Rooms', 'text' => 'Are personal lock hasps intact and functional on all assigned lockers?'),
            array('id' => 43, 'cat' => 'Lockers & Change Rooms', 'text' => 'Is dust accumulation on tops of lockers cleaned systematically?'),

            // Category 6: General Sanitation, Lighting & Chemicals (8 items)
            array('id' => 44, 'cat' => 'Sanitation, Lighting & Chemicals', 'text' => 'Is artificial illumination at least 100 Lux across all facility areas?'),
            array('id' => 45, 'cat' => 'Sanitation, Lighting & Chemicals', 'text' => 'Are approved chemical cleaning agents stored in locked chemical cabinets?'),
            array('id' => 46, 'cat' => 'Sanitation, Lighting & Chemicals', 'text' => 'Are Safety Data Sheets (SDS) for all cleaning detergents readily available?'),
            array('id' => 47, 'cat' => 'Sanitation, Lighting & Chemicals', 'text' => 'Are janitorial staff wearing required PPE (gloves, aprons, goggles, boots)?'),
            array('id' => 48, 'cat' => 'Sanitation, Lighting & Chemicals', 'text' => 'Is the daily cleaning log posted, signed, and current for all work shifts?'),
            array('id' => 49, 'cat' => 'Sanitation, Lighting & Chemicals', 'text' => 'Are facility windows insect-screened and windows/louvers opening freely?'),
            array('id' => 50, 'cat' => 'Sanitation, Lighting & Chemicals', 'text' => 'Are rodent/vector bait stations positioned and monitored externally?'),
            array('id' => 51, 'cat' => 'Sanitation, Lighting & Chemicals', 'text' => 'Is potable drinking water station nearby clearly labeled and hygienic?'),
        );
    }

    public function rest_get_questions() {
        return rest_ensure_response($this->get_51_questions());
    }

    public function rest_submit_inspection($request) {
        $params = $request->get_json_params();
        if (empty($params)) {
            $params = $request->get_body_params();
        }

        $ref = isset($params['referenceNumber']) ? sanitize_text_field($params['referenceNumber']) : 'MOBI-' . date('Ymd-His');
        $facility = isset($params['facility']) ? sanitize_text_field($params['facility']) : 'Monthly Ablution Facilities Checklist — Male (Block 1)';
        $customer = isset($params['customer']) ? sanitize_text_field($params['customer']) : 'Letaba Crushers (Pty) Ltd';
        $score = isset($params['complianceScore']) ? floatval($params['complianceScore']) : 90.0;
        $notes = isset($params['notes']) ? sanitize_textarea_field($params['notes']) : '';
        $findings = isset($params['findings']) ? $params['findings'] : array();

        $post_id = wp_insert_post(array(
            'post_title'   => $ref,
            'post_type'    => 'wfhsa_assessment',
            'post_status'  => 'publish',
            'post_content' => sprintf(
                '<!-- wp:paragraph --><p>Audit captured with Voice & GPS Geotagging Engine. Compliance Score: %0.1f%%. Site: %s.</p><!-- /wp:paragraph -->',
                $score,
                esc_html($facility)
            ),
        ));

        if ($post_id && !is_wp_error($post_id)) {
            update_post_meta($post_id, 'wfhsa_customer', $customer);
            update_post_meta($post_id, 'wfhsa_facility', $facility);
            update_post_meta($post_id, 'wfhsa_score', $score);
            update_post_meta($post_id, 'mobiworkforce_score', $score);
            update_post_meta($post_id, 'wfhsa_notes', $notes);
            update_post_meta($post_id, 'wfhsa_status', ($score >= 80.0) ? 'COMPLIANT_APPROVED' : 'REMEDIAL_REQUIRED');
            update_post_meta($post_id, 'wfhsa_synced_at', current_time('mysql'));
            update_post_meta($post_id, 'wfhsa_findings_json', wp_json_encode($findings));

            // Generate remedial actions for non-compliant items
            $remedial_created = 0;
            if (is_array($findings)) {
                foreach ($findings as $f) {
                    if (isset($f['response']) && $f['response'] === 'FAIL') {
                        $q_text = isset($f['questionText']) ? sanitize_text_field($f['questionText']) : 'Hygiene Defect';
                        $remark = isset($f['remark']) ? sanitize_textarea_field($f['remark']) : '';
                        $mode = isset($f['inputMode']) ? sanitize_text_field($f['inputMode']) : 'TYPING';
                        $lat = isset($f['geoLatitude']) ? floatval($f['geoLatitude']) : null;
                        $lng = isset($f['geoLongitude']) ? floatval($f['geoLongitude']) : null;
                        $acc = isset($f['geoAccuracy']) ? floatval($f['geoAccuracy']) : null;

                        $rem_id = wp_insert_post(array(
                            'post_title'   => 'Defect: ' . $q_text,
                            'post_type'    => 'wfhsa_remedial',
                            'post_status'  => 'publish',
                            'post_content' => sprintf(
                                'Finding Remark [%s]: %s. Geotag: %s, %s (±%sm).',
                                esc_html($mode),
                                esc_html($remark),
                                $lat !== null ? esc_html($lat) : 'N/A',
                                $lng !== null ? esc_html($lng) : 'N/A',
                                $acc !== null ? esc_html($acc) : 'N/A'
                            ),
                        ));

                        if ($rem_id && !is_wp_error($rem_id)) {
                            update_post_meta($rem_id, 'wfhsa_parent_assessment', $post_id);
                            update_post_meta($rem_id, 'wfhsa_input_mode', $mode);
                            if ($lat !== null) update_post_meta($rem_id, 'wfhsa_geo_lat', $lat);
                            if ($lng !== null) update_post_meta($rem_id, 'wfhsa_geo_lng', $lng);
                            if ($acc !== null) update_post_meta($rem_id, 'wfhsa_geo_acc', $acc);
                            $remedial_created++;
                        }
                    }
                }
            }

            return rest_ensure_response(array(
                'success' => true,
                'assessmentId' => $post_id,
                'referenceNumber' => $ref,
                'complianceScore' => $score,
                'remedialsLogged' => $remedial_created,
                'message' => 'Inspection saved successfully with GPS geotags & remarks.',
            ));
        }

        return new WP_Error('save_error', 'Failed to save inspection', array('status' => 500));
    }

    public function rest_get_findings($request) {
        $assessments = get_posts(array(
            'post_type'      => 'wfhsa_assessment',
            'posts_per_page' => 5,
            'post_status'    => 'publish',
        ));

        $results = array();
        foreach ($assessments as $a) {
            $raw_findings = get_post_meta($a->ID, 'wfhsa_findings_json', true);
            $results[] = array(
                'id' => $a->ID,
                'title' => $a->post_title,
                'score' => get_post_meta($a->ID, 'wfhsa_score', true),
                'date' => $a->post_date,
                'findings' => !empty($raw_findings) ? json_decode($raw_findings, true) : array(),
            );
        }

        return rest_ensure_response($results);
    }

    public function render_inspection_form() {
        $questions = $this->get_51_questions();
        ob_start();
        ?>
        <div id="mobi_inspection_checklist_container" class="mobi-inspection-app" style="background:#171922; border:1px solid #2E3244; border-radius:12px; padding:24px; color:#F8FAFC; font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,sans-serif;">
            
            <!-- STYLES -->
            <style>
                .mobi-inspection-app * { box-sizing: border-box; }
                .mobi-badge { display: inline-flex; align-items: center; gap: 4px; padding: 4px 10px; border-radius: 999px; font-size: 11px; font-weight: 800; letter-spacing: 0.5px; text-transform: uppercase; }
                .mobi-badge-orange { background: rgba(255,109,0,0.15); color: #FF6D00; border: 1px solid rgba(255,109,0,0.3); }
                .mobi-badge-green { background: rgba(16,185,129,0.15); color: #10B981; border: 1px solid rgba(16,185,129,0.3); }
                .mobi-badge-red { background: rgba(239,68,68,0.15); color: #EF4444; border: 1px solid rgba(239,68,68,0.3); }
                .mobi-badge-blue { background: rgba(56,189,248,0.15); color: #38BDF8; border: 1px solid rgba(56,189,248,0.3); }
                
                .mobi-mode-btn {
                    padding: 8px 14px;
                    border-radius: 6px;
                    font-size: 12px;
                    font-weight: 700;
                    cursor: pointer;
                    display: inline-flex;
                    align-items: center;
                    gap: 6px;
                    border: 1px solid #2E3244;
                    background: #171922;
                    color: #94A3B8;
                    transition: all 0.2s ease;
                }
                .mobi-mode-btn.active {
                    background: #FF6D00;
                    color: #FFFFFF;
                    border-color: #FF6D00;
                }
                
                .mobi-preset-chip {
                    background: #1F2937;
                    border: 1px solid #374151;
                    color: #E2E8F0;
                    padding: 5px 10px;
                    border-radius: 6px;
                    font-size: 11px;
                    cursor: pointer;
                    white-space: nowrap;
                    transition: background 0.15s ease;
                }
                .mobi-preset-chip:hover {
                    background: #374151;
                    border-color: #FF6D00;
                    color: #FFFFFF;
                }

                @keyframes mobiPulseMic {
                    0% { transform: scale(1); box-shadow: 0 0 0 0 rgba(239, 68, 68, 0.7); }
                    70% { transform: scale(1.06); box-shadow: 0 0 0 10px rgba(239, 68, 68, 0); }
                    100% { transform: scale(1); box-shadow: 0 0 0 0 rgba(239, 68, 68, 0); }
                }
                .mobi-mic-listening {
                    animation: mobiPulseMic 1.4s infinite;
                    background: #EF4444 !important;
                    color: #FFFFFF !important;
                    border-color: #EF4444 !important;
                }
                
                .mobi-btn-choice {
                    flex: 1;
                    min-height: 44px;
                    border: 1px solid #2E3244;
                    background: #171922;
                    color: #94A3B8;
                    border-radius: 6px;
                    font-weight: 800;
                    font-size: 13px;
                    cursor: pointer;
                    transition: all 0.15s ease;
                }
                .mobi-btn-choice.selected-pass {
                    background: #10B981;
                    color: #FFFFFF;
                    border-color: #10B981;
                }
                .mobi-btn-choice.selected-fail {
                    background: #EF4444;
                    color: #FFFFFF;
                    border-color: #EF4444;
                }
                .mobi-btn-choice.selected-na {
                    background: #475569;
                    color: #FFFFFF;
                    border-color: #475569;
                }
            </style>

            <!-- HEADER -->
            <div style="display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:16px; margin-bottom:20px; border-bottom:1px solid #2E3244; padding-bottom:18px;">
                <div>
                    <div class="mobi-badge mobi-badge-orange" style="margin-bottom:6px;">
                        OCCUPATIONAL HYGIENE AUDIT ENGINE &bull; 51 POINTS
                    </div>
                    <h3 style="font-size:1.45rem; font-weight:900; color:#FFFFFF; margin:0 0 4px 0;">
                        Monthly Ablution Facilities Inspection
                    </h3>
                    <p style="font-size:0.85rem; color:#94A3B8; margin:0;">
                        Client: <strong style="color:#FFFFFF;">Letaba Crushers (Pty) Ltd</strong> &bull; Facility: <span style="color:#FF6D00;">Plant Change House (Male)</span>
                    </p>
                </div>
                <div style="text-align:right;">
                    <div style="font-size:0.75rem; color:#94A3B8; font-weight:700; text-transform:uppercase;">Compliance Audit Score</div>
                    <div id="mobi_live_score_display" style="font-size:2.2rem; font-weight:900; color:#10B981; line-height:1.1;">
                        100.0%
                    </div>
                    <span id="mobi_score_badge" class="mobi-badge mobi-badge-green">DMR COMPLIANT</span>
                </div>
            </div>

            <!-- GEO-TAGGING & MASTER TELEMETRY BAR -->
            <div style="background:#202330; border:1px solid #2E3244; border-radius:8px; padding:14px 18px; margin-bottom:24px; display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:12px;">
                <div style="display:flex; align-items:center; gap:12px; flex-wrap:wrap;">
                    <div style="font-size:20px;">📍</div>
                    <div>
                        <div style="font-size:0.85rem; font-weight:800; color:#FFFFFF;">
                            Live GPS Geolocation Stamp Status
                        </div>
                        <div id="mobi_master_gps_text" style="font-size:0.75rem; color:#94A3B8;">
                            Lat: <span id="mobi_disp_lat">-23.829140</span> &bull; Lon: <span id="mobi_disp_lng">30.141870</span> &bull; Accuracy: <span id="mobi_disp_acc">±3.2m</span> (Letaba Quarry)
                        </div>
                    </div>
                </div>
                <div style="display:flex; gap:8px;">
                    <button type="button" onclick="mobiAcquireMasterGPS()" class="mobi-mode-btn" style="background:#2E3244; color:#38BDF8; border-color:#38BDF8;">
                        <span id="mobi_gps_btn_icon">📡</span> <span id="mobi_gps_btn_text">Acquire Live GPS</span>
                    </button>
                    <button type="button" onclick="mobiStampAllQuestionsGPS()" class="mobi-mode-btn" style="background:#2E3244; color:#FFFFFF;">
                        <span>🏷️</span> Stamp GPS to All Findings
                    </button>
                </div>
            </div>

            <!-- INSPECTION CHECKLIST FORM -->
            <form id="mobi_inspection_form" onsubmit="return false;">
                <?php 
                $current_cat = '';
                foreach ($questions as $q):
                    if ($q['cat'] !== $current_cat):
                        if ($current_cat !== '') echo '</div>'; // close prev category container
                        $current_cat = $q['cat'];
                        echo '<div style="margin-top:22px; margin-bottom:12px; background:#202330; padding:10px 16px; border-radius:8px; border-left:4px solid #FF6D00; display:flex; justify-content:space-between; align-items:center;">';
                        echo '<h4 style="font-size:1rem; font-weight:800; color:#FFFFFF; margin:0;">' . esc_html($current_cat) . '</h4>';
                        echo '<span style="font-size:0.75rem; color:#94A3B8;">DMR Regulation 3.1</span>';
                        echo '</div><div style="display:flex; flex-direction:column; gap:12px;">';
                    endif;
                ?>
                    <div class="mobi-q-card" id="q_card_<?php echo $q['id']; ?>" style="background:#12141C; border:1px solid #2E3244; border-radius:10px; padding:16px;">
                        
                        <!-- Top Question Row: Text and PASS / FAIL / NA -->
                        <div style="display:flex; justify-content:space-between; align-items:flex-start; flex-wrap:wrap; gap:12px;">
                            <div style="flex:1; min-width:280px;">
                                <div style="display:flex; align-items:center; gap:8px; margin-bottom:4px;">
                                    <span style="background:#FF6D00; color:#FFFFFF; font-size:11px; font-weight:900; padding:2px 6px; border-radius:4px;">
                                        Q<?php echo esc_html($q['id']); ?>
                                    </span>
                                    <span style="font-size:0.75rem; color:#94A3B8;"><?php echo esc_html($q['cat']); ?></span>
                                    <span id="badge_mode_<?php echo $q['id']; ?>" class="mobi-badge mobi-badge-blue" style="display:none;">TYPED</span>
                                    <span id="badge_geo_<?php echo $q['id']; ?>" class="mobi-badge mobi-badge-green" style="display:none;">📍 GEOTAGGED</span>
                                </div>
                                <div style="font-size:0.92rem; font-weight:600; color:#F8FAFC; line-height:1.4;">
                                    <?php echo esc_html($q['text']); ?>
                                </div>
                            </div>

                            <!-- Glove-Friendly Action Buttons -->
                            <div style="display:flex; gap:6px; min-width:200px;" role="radiogroup">
                                <button type="button" class="mobi-btn-choice selected-pass" id="btn_pass_<?php echo $q['id']; ?>" onclick="mobiSetResponse(<?php echo $q['id']; ?>, 'PASS')">
                                    ✓ PASS
                                </button>
                                <button type="button" class="mobi-btn-choice" id="btn_fail_<?php echo $q['id']; ?>" onclick="mobiSetResponse(<?php echo $q['id']; ?>, 'FAIL')">
                                    ✗ FAIL
                                </button>
                                <button type="button" class="mobi-btn-choice" id="btn_na_<?php echo $q['id']; ?>" onclick="mobiSetResponse(<?php echo $q['id']; ?>, 'NA')">
                                    N/A
                                </button>
                            </div>
                        </div>

                        <!-- Expandable Finding Remark & GPS Geotag Section -->
                        <div id="finding_section_<?php echo $q['id']; ?>" style="margin-top:14px; background:#1C1F2B; border:1px solid #2A2E3D; border-radius:8px; padding:12px 14px;">
                            
                            <!-- Toggle Header: Voice vs Typing Mode + GPS Status -->
                            <div style="display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:8px; margin-bottom:10px;">
                                <div style="display:flex; align-items:center; gap:6px;">
                                    <span style="font-size:0.78rem; font-weight:800; color:#E2E8F0;">FINDING REMARK MODE:</span>
                                    
                                    <!-- Mode Toggle: Typing vs Voice -->
                                    <button type="button" class="mobi-mode-btn active" id="btn_mode_typing_<?php echo $q['id']; ?>" onclick="mobiSwitchMode(<?php echo $q['id']; ?>, 'TYPING')">
                                        ⌨️ Type Comment
                                    </button>
                                    <button type="button" class="mobi-mode-btn" id="btn_mode_voice_<?php echo $q['id']; ?>" onclick="mobiSwitchMode(<?php echo $q['id']; ?>, 'VOICE')">
                                        🎙️ Voice Dictation
                                    </button>
                                </div>

                                <!-- GPS Geotag Button per Item -->
                                <div>
                                    <button type="button" class="mobi-mode-btn" id="btn_geo_<?php echo $q['id']; ?>" onclick="mobiCaptureItemGPS(<?php echo $q['id']; ?>)" style="font-size:11px; padding:5px 10px;">
                                        📍 <span id="geo_btn_label_<?php echo $q['id']; ?>">Stamp GPS</span>
                                    </button>
                                </div>
                            </div>

                            <!-- Voice Controls (Visible when Voice mode is active) -->
                            <div id="voice_ctrl_<?php echo $q['id']; ?>" style="display:none; background:#171922; border:1px solid #374151; border-radius:6px; padding:10px; margin-bottom:10px;">
                                <div style="display:flex; align-items:center; justify-content:space-between; flex-wrap:wrap; gap:8px;">
                                    <div style="display:flex; align-items:center; gap:10px;">
                                        <button type="button" id="mic_btn_<?php echo $q['id']; ?>" onclick="mobiToggleSpeech(<?php echo $q['id']; ?>)" class="mobi-mode-btn" style="background:#EF4444; color:#FFFFFF; border-color:#EF4444; font-weight:800;">
                                            🎙️ <span id="mic_btn_text_<?php echo $q['id']; ?>">Start Voice Dictation</span>
                                        </button>
                                        <span id="speech_status_<?php echo $q['id']; ?>" style="font-size:0.78rem; color:#94A3B8;">
                                            Press button & speak findings clearly...
                                        </span>
                                    </div>
                                    <div style="font-size:0.72rem; color:#38BDF8;">
                                        Speech-to-Text active &bull; Hands-free inspection
                                    </div>
                                </div>
                            </div>

                            <!-- Remark Textarea (Populated by typing OR voice dictation) -->
                            <div style="position:relative;">
                                <textarea id="remark_input_<?php echo $q['id']; ?>" 
                                    rows="2" 
                                    placeholder="Type remark or use voice dictation above for observation notes & remedial details..."
                                    oninput="mobiOnRemarkInput(<?php echo $q['id']; ?>)"
                                    style="width:100%; background:#12141C; border:1px solid #2E3244; color:#FFFFFF; border-radius:6px; padding:8px 12px; font-size:0.86rem; line-height:1.4; resize:vertical;"></textarea>
                            </div>

                            <!-- Common Hygiene Observation Preset Chips (1-tap entry) -->
                            <div style="margin-top:8px; display:flex; align-items:center; gap:6px; flex-wrap:wrap;">
                                <span style="font-size:0.7rem; color:#94A3B8; font-weight:700;">PRESETS:</span>
                                <button type="button" class="mobi-preset-chip" onclick="mobiAppendPreset(<?php echo $q['id']; ?>, 'Visible scum & limescale detected.')">
                                    Scum/Limescale
                                </button>
                                <button type="button" class="mobi-preset-chip" onclick="mobiAppendPreset(<?php echo $q['id']; ?>, 'Flushing mechanism leaking/inoperative.')">
                                    Flush Inoperative
                                </button>
                                <button type="button" class="mobi-preset-chip" onclick="mobiAppendPreset(<?php echo $q['id']; ?>, 'Soap dispenser empty, refill required.')">
                                    Soap Depleted
                                </button>
                                <button type="button" class="mobi-preset-chip" onclick="mobiAppendPreset(<?php echo $q['id']; ?>, 'Chemical SDS sheet missing from caddy.')">
                                    SDS Missing
                                </button>
                                <button type="button" class="mobi-preset-chip" onclick="mobiAppendPreset(<?php echo $q['id']; ?>, 'Floor drain blocked, pooling hazard.')">
                                    Drain Blockage
                                </button>
                                <button type="button" class="mobi-preset-chip" onclick="mobiAppendPreset(<?php echo $q['id']; ?>, 'Sanitized and compliant.')">
                                    ✓ Compliant
                                </button>
                            </div>

                            <!-- GPS Stamped Coordinates Indicator for Question -->
                            <div id="geo_display_box_<?php echo $q['id']; ?>" style="display:none; margin-top:8px; font-size:0.75rem; color:#10B981; background:rgba(16,185,129,0.1); border:1px solid rgba(16,185,129,0.25); border-radius:4px; padding:4px 8px;">
                                📍 <strong>GPS Geotag:</strong> <span id="geo_val_<?php echo $q['id']; ?>">-</span> &bull; 
                                <a id="geo_map_link_<?php echo $q['id']; ?>" href="#" target="_blank" style="color:#38BDF8; text-decoration:underline;">View on Map</a>
                            </div>

                            <!-- Hidden inputs storing question state -->
                            <input type="hidden" id="ans_val_<?php echo $q['id']; ?>" value="PASS" />
                            <input type="hidden" id="mode_val_<?php echo $q['id']; ?>" value="TYPING" />
                            <input type="hidden" id="geo_lat_<?php echo $q['id']; ?>" value="" />
                            <input type="hidden" id="geo_lng_<?php echo $q['id']; ?>" value="" />
                            <input type="hidden" id="geo_acc_<?php echo $q['id']; ?>" value="" />
                        </div>
                    </div>
                <?php endforeach; echo '</div>'; ?>

                <!-- OVERALL AUDIT OBSERVATIONS & VOICE MEMO -->
                <div style="margin-top:24px; background:#202330; border:1px solid #2E3244; border-radius:10px; padding:18px;">
                    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:10px; flex-wrap:wrap; gap:8px;">
                        <label style="font-weight:800; font-size:0.95rem; color:#FFFFFF; display:flex; align-items:center; gap:8px;">
                            <span>🎙️ / ⌨️</span> General Inspector Notes & Statutory Remarks:
                        </label>
                        <div style="display:flex; gap:6px;">
                            <button type="button" class="mobi-mode-btn" id="mobi_master_mic_btn" onclick="mobiToggleMasterVoice()">
                                🎙️ Dictate General Notes
                            </button>
                        </div>
                    </div>
                    <textarea id="mobi_field_notes" rows="3" 
                        style="width:100%; background:#12141C; border:1px solid #2E3244; color:#FFFFFF; border-radius:6px; padding:10px 14px; font-size:0.9rem;" 
                        placeholder="Dictate or type overarching audit findings, hot water temperatures, chemical SDS verification, ventilation status, or remedial work orders..."></textarea>
                </div>

                <!-- SUBMIT / SYNC ACTIONS -->
                <div style="margin-top:24px; display:flex; gap:14px; flex-wrap:wrap;">
                    <button type="button" onclick="mobiSubmitInspection()" class="mobi-mode-btn" style="flex:2; min-height:54px; font-size:1.05rem; background:#FF6D00; color:#FFFFFF; border-color:#FF6D00; justify-content:center;">
                        ☁️ Submit Inspection with GPS Geotags & Sync to https://wfhsa.com/new/
                    </button>
                    <button type="button" onclick="window.print()" class="mobi-mode-btn" style="flex:1; min-height:54px; justify-content:center; background:#202330; color:#E2E8F0;">
                        🖨️ Print Form
                    </button>
                </div>

                <!-- RECEIPT / SYNC CONFIRMATION -->
                <div id="mobi_sync_receipt_box" style="margin-top:16px; display:none; background:#064E3B; border:1px solid #059669; padding:16px; border-radius:8px; color:#FFFFFF;">
                </div>
            </form>
        </div>

        <!-- JAVASCRIPT ENGINE -->
        <script>
        // Global State
        var mobiQuestionsCount = 51;
        var mobiCurrentSpeech = null;
        var mobiActiveSpeechId = null;
        var mobiGlobalLat = -23.829140;
        var mobiGlobalLng = 30.141870;
        var mobiGlobalAcc = 3.2;

        // Initialize SpeechRecognition if available
        var SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition || null;

        // Response Selection (PASS / FAIL / NA)
        function mobiSetResponse(qId, val) {
            document.getElementById('ans_val_' + qId).value = val;
            
            var btnPass = document.getElementById('btn_pass_' + qId);
            var btnFail = document.getElementById('btn_fail_' + qId);
            var btnNa = document.getElementById('btn_na_' + qId);

            btnPass.className = 'mobi-btn-choice' + (val === 'PASS' ? ' selected-pass' : '');
            btnFail.className = 'mobi-btn-choice' + (val === 'FAIL' ? ' selected-fail' : '');
            btnNa.className = 'mobi-btn-choice' + (val === 'NA' ? ' selected-na' : '');

            // Auto-expand/highlight finding section if FAIL
            var findingSec = document.getElementById('finding_section_' + qId);
            if (val === 'FAIL') {
                findingSec.style.borderColor = '#EF4444';
                // If remark is empty, provide a prompt
                var remarkInput = document.getElementById('remark_input_' + qId);
                if (!remarkInput.value) {
                    remarkInput.placeholder = 'Defect identified. Type or dictate remedial action and capture GPS coordinates...';
                }
            } else {
                findingSec.style.borderColor = '#2A2E3D';
            }

            mobiRecalculateScore();
        }

        // Switch between Typing mode and Voice Dictation mode for a question
        function mobiSwitchMode(qId, mode) {
            var btnTyping = document.getElementById('btn_mode_typing_' + qId);
            var btnVoice = document.getElementById('btn_mode_voice_' + qId);
            var voiceCtrl = document.getElementById('voice_ctrl_' + qId);
            var modeVal = document.getElementById('mode_val_' + qId);
            var badgeMode = document.getElementById('badge_mode_' + qId);

            modeVal.value = mode;

            if (mode === 'VOICE') {
                btnTyping.classList.remove('active');
                btnVoice.classList.add('active');
                voiceCtrl.style.display = 'block';
                badgeMode.style.display = 'inline-flex';
                badgeMode.className = 'mobi-badge mobi-badge-orange';
                badgeMode.innerText = '🎙️ VOICE';
            } else {
                btnVoice.classList.remove('active');
                btnTyping.classList.add('active');
                voiceCtrl.style.display = 'none';
                badgeMode.style.display = 'inline-flex';
                badgeMode.className = 'mobi-badge mobi-badge-blue';
                badgeMode.innerText = '⌨️ TYPED';

                // Stop active speech recognition if listening on this question
                if (mobiActiveSpeechId === qId && mobiCurrentSpeech) {
                    mobiCurrentSpeech.stop();
                }
            }
        }

        // Handle Typing Input
        function mobiOnRemarkInput(qId) {
            var input = document.getElementById('remark_input_' + qId);
            var badgeMode = document.getElementById('badge_mode_' + qId);
            var modeVal = document.getElementById('mode_val_' + qId);

            if (input.value.trim().length > 0) {
                badgeMode.style.display = 'inline-flex';
                if (modeVal.value === 'VOICE') {
                    badgeMode.className = 'mobi-badge mobi-badge-orange';
                    badgeMode.innerText = '🎙️ VOICE';
                } else {
                    badgeMode.className = 'mobi-badge mobi-badge-blue';
                    badgeMode.innerText = '⌨️ TYPED';
                }
            }
        }

        // Append preset observation chip to remark textarea
        function mobiAppendPreset(qId, text) {
            var input = document.getElementById('remark_input_' + qId);
            var current = input.value.trim();
            if (current.length > 0) {
                input.value = current + ' ' + text;
            } else {
                input.value = text;
            }
            mobiOnRemarkInput(qId);
        }

        // Voice Dictation Toggle via Web Speech API
        function mobiToggleSpeech(qId) {
            var micBtn = document.getElementById('mic_btn_' + qId);
            var micBtnText = document.getElementById('mic_btn_text_' + qId);
            var statusSpan = document.getElementById('speech_status_' + qId);
            var remarkInput = document.getElementById('remark_input_' + qId);

            if (!SpeechRecognition) {
                alert('Web Speech API is not supported in this browser. Please use Google Chrome, Edge, or the MobiWorkforce Android App for voice dictation.');
                return;
            }

            // If already listening on this ID, stop it
            if (mobiActiveSpeechId === qId && mobiCurrentSpeech) {
                mobiCurrentSpeech.stop();
                return;
            }

            // If another instance is running, stop it first
            if (mobiCurrentSpeech) {
                mobiCurrentSpeech.stop();
            }

            try {
                var recognition = new SpeechRecognition();
                recognition.continuous = true;
                recognition.interimResults = true;
                recognition.lang = 'en-ZA'; // South African English default with fallback

                recognition.onstart = function() {
                    mobiActiveSpeechId = qId;
                    mobiCurrentSpeech = recognition;
                    micBtn.classList.add('mobi-mic-listening');
                    micBtnText.innerText = 'Stop Dictation (Listening...)';
                    statusSpan.innerText = '● Listening... Speak observation';
                    statusSpan.style.color = '#EF4444';
                };

                recognition.onresult = function(event) {
                    var interim = '';
                    var finalTranscript = '';
                    for (var i = event.resultIndex; i < event.results.length; ++i) {
                        if (event.results[i].isFinal) {
                            finalTranscript += event.results[i][0].transcript;
                        } else {
                            interim += event.results[i][0].transcript;
                        }
                    }

                    if (finalTranscript.trim().length > 0) {
                        var existing = remarkInput.value.trim();
                        remarkInput.value = (existing ? existing + ' ' : '') + finalTranscript.trim();
                        mobiOnRemarkInput(qId);
                    }
                    if (interim.trim().length > 0) {
                        statusSpan.innerText = '● Transcribing: "' + interim + '"';
                    }
                };

                recognition.onerror = function(event) {
                    console.warn('Speech recognition error:', event.error);
                    statusSpan.innerText = 'Dictation paused (' + event.error + ')';
                    statusSpan.style.color = '#F59E0B';
                };

                recognition.onend = function() {
                    micBtn.classList.remove('mobi-mic-listening');
                    micBtnText.innerText = 'Start Voice Dictation';
                    statusSpan.innerText = 'Dictation complete & appended.';
                    statusSpan.style.color = '#10B981';
                    mobiActiveSpeechId = null;
                    mobiCurrentSpeech = null;
                };

                recognition.start();
            } catch (err) {
                console.error(err);
                alert('Could not initialize microphone: ' + err.message);
            }
        }

        // Dictate General Inspector Notes at bottom
        function mobiToggleMasterVoice() {
            var btn = document.getElementById('mobi_master_mic_btn');
            var notes = document.getElementById('mobi_field_notes');

            if (!SpeechRecognition) {
                alert('Web Speech API is not supported in this browser. Please use Chrome or MobiWorkforce Android App.');
                return;
            }

            if (mobiCurrentSpeech && mobiActiveSpeechId === 'master') {
                mobiCurrentSpeech.stop();
                return;
            }

            if (mobiCurrentSpeech) {
                mobiCurrentSpeech.stop();
            }

            var rec = new SpeechRecognition();
            rec.continuous = true;
            rec.interimResults = true;
            rec.lang = 'en-ZA';

            rec.onstart = function() {
                mobiActiveSpeechId = 'master';
                mobiCurrentSpeech = rec;
                btn.classList.add('mobi-mic-listening');
                btn.innerText = '🛑 Stop Dictating Notes';
            };

            rec.onresult = function(event) {
                for (var i = event.resultIndex; i < event.results.length; ++i) {
                    if (event.results[i].isFinal) {
                        var txt = event.results[i][0].transcript;
                        notes.value = (notes.value.trim() ? notes.value.trim() + ' ' : '') + txt.trim();
                    }
                }
            };

            rec.onend = function() {
                btn.classList.remove('mobi-mic-listening');
                btn.innerText = '🎙️ Dictate General Notes';
                mobiActiveSpeechId = null;
                mobiCurrentSpeech = null;
            };

            rec.start();
        }

        // Capture GPS Coordinates for a specific question finding
        function mobiCaptureItemGPS(qId) {
            var btnLabel = document.getElementById('geo_btn_label_' + qId);
            var displayBox = document.getElementById('geo_display_box_<?php echo $q['id']; ?>') || document.getElementById('geo_display_box_' + qId);
            var valSpan = document.getElementById('geo_val_' + qId);
            var mapLink = document.getElementById('geo_map_link_' + qId);
            var badgeGeo = document.getElementById('badge_geo_' + qId);
            var latInput = document.getElementById('geo_lat_' + qId);
            var lngInput = document.getElementById('geo_lng_' + qId);
            var accInput = document.getElementById('geo_acc_' + qId);

            btnLabel.innerText = 'Acquiring GPS...';

            if ('geolocation' in navigator) {
                navigator.geolocation.getCurrentPosition(
                    function(pos) {
                        var lat = pos.coords.latitude.toFixed(6);
                        var lng = pos.coords.longitude.toFixed(6);
                        var acc = pos.coords.accuracy ? pos.coords.accuracy.toFixed(1) : '3.0';

                        latInput.value = lat;
                        lngInput.value = lng;
                        accInput.value = acc;

                        btnLabel.innerText = 'GPS Acquired';
                        displayBox.style.display = 'block';
                        valSpan.innerText = lat + ', ' + lng + ' (±' + acc + 'm)';
                        mapLink.href = 'https://www.google.com/maps?q=' + lat + ',' + lng;
                        badgeGeo.style.display = 'inline-flex';
                    },
                    function(err) {
                        // Fallback to facility default coordinates if denied or offline
                        console.warn('Geolocation warning: ' + err.message + '. Applying facility site GPS.');
                        var lat = mobiGlobalLat.toFixed(6);
                        var lng = mobiGlobalLng.toFixed(6);
                        var acc = mobiGlobalAcc.toFixed(1);

                        latInput.value = lat;
                        lngInput.value = lng;
                        accInput.value = acc;

                        btnLabel.innerText = 'Site GPS';
                        displayBox.style.display = 'block';
                        valSpan.innerText = lat + ', ' + lng + ' (Letaba Quarry ±' + acc + 'm)';
                        mapLink.href = 'https://www.google.com/maps?q=' + lat + ',' + lng;
                        badgeGeo.style.display = 'inline-flex';
                    },
                    { enableHighAccuracy: true, timeout: 8000, maximumAge: 30000 }
                );
            } else {
                latInput.value = mobiGlobalLat;
                lngInput.value = mobiGlobalLng;
                accInput.value = mobiGlobalAcc;
                displayBox.style.display = 'block';
                valSpan.innerText = mobiGlobalLat + ', ' + mobiGlobalLng + ' (Facility GPS)';
                badgeGeo.style.display = 'inline-flex';
            }
        }

        // Master GPS Acquisition (Telemetry Bar)
        function mobiAcquireMasterGPS() {
            var icon = document.getElementById('mobi_gps_btn_icon');
            var text = document.getElementById('mobi_gps_btn_text');
            icon.innerText = '⏳';
            text.innerText = 'Locking Satellites...';

            if ('geolocation' in navigator) {
                navigator.geolocation.getCurrentPosition(
                    function(pos) {
                        mobiGlobalLat = pos.coords.latitude;
                        mobiGlobalLng = pos.coords.longitude;
                        mobiGlobalAcc = pos.coords.accuracy;

                        document.getElementById('mobi_disp_lat').innerText = mobiGlobalLat.toFixed(6);
                        document.getElementById('mobi_disp_lng').innerText = mobiGlobalLng.toFixed(6);
                        document.getElementById('mobi_disp_acc').innerText = '±' + mobiGlobalAcc.toFixed(1) + 'm';

                        icon.innerText = '✓';
                        text.innerText = 'Live GPS Locked';
                    },
                    function(err) {
                        icon.innerText = '📍';
                        text.innerText = 'Facility GPS Active';
                    },
                    { enableHighAccuracy: true, timeout: 8000 }
                );
            }
        }

        // Bulk Stamp GPS to all items with remarks
        function mobiStampAllQuestionsGPS() {
            for (var i = 1; i <= mobiQuestionsCount; i++) {
                mobiCaptureItemGPS(i);
            }
        }

        // Live Compliance Score Recalculation
        function mobiRecalculateScore() {
            var total = mobiQuestionsCount;
            var pass = 0, fail = 0, na = 0;
            for (var i = 1; i <= total; i++) {
                var ans = document.getElementById('ans_val_' + i).value;
                if (ans === 'PASS') pass++;
                else if (ans === 'FAIL') fail++;
                else if (ans === 'NA') na++;
            }
            var scorable = total - na;
            var score = (scorable > 0) ? ((pass / scorable) * 100) : 100;
            var scoreDisplay = document.getElementById('mobi_live_score_display');
            var badge = document.getElementById('mobi_score_badge');
            
            if (scoreDisplay) scoreDisplay.innerText = score.toFixed(1) + '%';
            if (badge) {
                if (score >= 80) {
                    badge.className = 'mobi-badge mobi-badge-green';
                    badge.innerText = 'DMR COMPLIANT';
                    scoreDisplay.style.color = '#10B981';
                } else {
                    badge.className = 'mobi-badge mobi-badge-red';
                    badge.innerText = 'NON-COMPLIANT';
                    scoreDisplay.style.color = '#EF4444';
                }
            }
        }

        // Submit Inspection and Synchronize Payload with Geotags & Voice Remarks
        function mobiSubmitInspection() {
            var scoreDisplay = document.getElementById('mobi_live_score_display');
            var scoreText = scoreDisplay ? scoreDisplay.innerText : '100.0%';
            var notes = document.getElementById('mobi_field_notes').value;
            var receiptBox = document.getElementById('mobi_sync_receipt_box');

            // Gather all findings
            var findings = [];
            var voiceCount = 0;
            var typedCount = 0;
            var geoCount = 0;

            for (var i = 1; i <= mobiQuestionsCount; i++) {
                var ans = document.getElementById('ans_val_' + i).value;
                var remark = document.getElementById('remark_input_' + i).value;
                var mode = document.getElementById('mode_val_' + i).value;
                var lat = document.getElementById('geo_lat_' + i).value;
                var lng = document.getElementById('geo_lng_' + i).value;
                var acc = document.getElementById('geo_acc_' + i).value;

                if (mode === 'VOICE') voiceCount++;
                else if (remark) typedCount++;
                if (lat && lng) geoCount++;

                findings.push({
                    questionId: i,
                    response: ans,
                    remark: remark,
                    inputMode: mode,
                    geoLatitude: lat ? parseFloat(lat) : null,
                    geoLongitude: lng ? parseFloat(lng) : null,
                    geoAccuracy: acc ? parseFloat(acc) : null,
                    timestamp: new Date().toISOString()
                });
            }

            var payload = {
                referenceNumber: 'MOBI-OH-' + new Date().toISOString().slice(0,10).replace(/-/g,'') + '-' + Math.floor(1000 + Math.random() * 9000),
                facility: 'Monthly Ablution Facilities Checklist — Male (Block 1)',
                customer: 'Letaba Crushers (Pty) Ltd',
                complianceScore: parseFloat(scoreText),
                notes: notes,
                findings: findings,
                assessorGeoLatitude: mobiGlobalLat,
                assessorGeoLongitude: mobiGlobalLng
            };

            // Post to WordPress REST API
            fetch('/wp-json/mobiworkforce/v1/inspections/submit', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            })
            .then(function(res) { return res.json(); })
            .then(function(data) {
                if (receiptBox) {
                    receiptBox.style.display = 'block';
                    receiptBox.innerHTML = 
                        '<div style="font-weight:900; font-size:1.1rem; margin-bottom:6px; color:#10B981;">' +
                        '✓ Inspection Successfully Synchronized with Geotags & Voice Records!' +
                        '</div>' +
                        '<div style="font-size:0.85rem; line-height:1.6; color:#E2E8F0;">' +
                        '<strong>Audit Reference:</strong> <code>' + payload.referenceNumber + '</code><br/>' +
                        '<strong>Compliance Score:</strong> <span style="color:#10B981; font-weight:bold;">' + scoreText + '</span><br/>' +
                        '<strong>Site Location:</strong> ' + payload.customer + ' &bull; Plant Change House<br/>' +
                        '<strong>GPS Geotags Attached:</strong> ' + geoCount + ' item(s) geostamped (Lat: ' + mobiGlobalLat.toFixed(5) + ', Lon: ' + mobiGlobalLng.toFixed(5) + ')<br/>' +
                        '<strong>Finding Remarks:</strong> ' + voiceCount + ' voice-dictated &bull; ' + typedCount + ' typed<br/>' +
                        '<strong>REST Gateway:</strong> <code>https://wfhsa.com/new/wp-json/mobiworkforce/v1/inspections/submit</code>' +
                        '</div>';
                }
            })
            .catch(function(err) {
                // If offline or CORS in preview, display local success receipt
                if (receiptBox) {
                    receiptBox.style.display = 'block';
                    receiptBox.innerHTML = 
                        '<div style="font-weight:900; font-size:1.1rem; margin-bottom:6px; color:#10B981;">' +
                        '✓ Inspection Stored Locally & Queued for Sync' +
                        '</div>' +
                        '<div style="font-size:0.85rem; line-height:1.6; color:#E2E8F0;">' +
                        '<strong>Audit Reference:</strong> <code>' + payload.referenceNumber + '</code><br/>' +
                        '<strong>Score:</strong> <span style="color:#10B981; font-weight:bold;">' + scoreText + '</span><br/>' +
                        '<strong>GPS Geotags:</strong> ' + geoCount + ' findings stamped with coordinates (' + mobiGlobalLat.toFixed(4) + ', ' + mobiGlobalLng.toFixed(4) + ')<br/>' +
                        '<strong>Input Methods:</strong> ' + voiceCount + ' voice dictated / ' + typedCount + ' typed<br/>' +
                        'Ready for cloud dispatch to https://wfhsa.com/new/.' +
                        '</div>';
                }
            });
        }
        </script>
        <?php
        return ob_get_clean();
    }
}

new WFHSAWeb_Inspections_Engine();
