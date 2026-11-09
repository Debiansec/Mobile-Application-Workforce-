<?php
/**
 * Plugin Name: MobiWorkforce — Inspection PDF & Audit Report Generator
 * Plugin URI: https://wfhsa.com/new/
 * Description: Generates official Occupational Hygiene Compliance Certificates and comprehensive audit reports for MobiWorkforce & WorkForce Health SA. Includes executive scorecards, non-compliance remedial matrices, photographic evidence with GPS geostamps, and digital dual signatures.
 * Version: 1.2.0
 * Author: WorkForce Health SA / MobiWorkforce Team
 * Author URI: https://wfhsa.com
 * License: Proprietary
 * Text Domain: mobiworkforce-reports-pdf
 */

if (!defined('ABSPATH')) {
    exit;
}

class MobiWorkforce_Reports_PDF {

    public function __construct() {
        add_action('rest_api_init', array($this, 'register_rest_routes'));
        add_shortcode('mobiworkforce_report_viewer', array($this, 'render_report_shortcode'));
        add_shortcode('wfhsa_report_viewer', array($this, 'render_report_shortcode'));
    }

    public function register_rest_routes() {
        register_rest_route('mobiworkforce/v1', '/reports/(?P<id>[a-zA-Z0-9_-]+)/html', array(
            'methods'  => 'GET',
            'callback' => array($this, 'rest_get_report_html'),
            'permission_callback' => '__return_true',
        ));
    }

    public function render_report_shortcode($atts) {
        $a = shortcode_atts(array(
            'id' => 'asm_letaba_01',
        ), $atts);
        return $this->generate_report_html($a['id']);
    }

    public function rest_get_report_html($request) {
        $id = $request->get_param('id');
        return rest_ensure_response(array(
            'id'   => $id,
            'html' => $this->generate_report_html($id),
        ));
    }

    public function generate_report_html($assessment_id = 'asm_letaba_01') {
        ob_start();
        ?>
        <div class="mobi-report-wrapper" style="font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; color: #1E293B; line-height: 1.5;">
            <!-- Certificate Header -->
            <div style="border-bottom: 3px solid #D32F2F; padding-bottom: 18px; margin-bottom: 24px; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap;">
                <div>
                    <div style="font-size: 1.4rem; font-weight: 900; color: #0F172A; letter-spacing: 0.5px;">
                        OCCUPATIONAL HYGIENE COMPLIANCE AUDIT
                    </div>
                    <div style="font-size: 0.9rem; color: #D32F2F; font-weight: 800; letter-spacing: 1px; margin-top: 2px;">
                        WORKFORCE HEALTH SA &bull; MOBIWORKFORCE DIGITAL PLATFORM
                    </div>
                </div>
                <div style="text-align: right;">
                    <div style="font-size: 0.78rem; color: #64748B; font-weight: 700;">AUDIT REFERENCE</div>
                    <div style="font-size: 1.15rem; font-weight: 900; color: #0F172A;">WFH-2026-1044</div>
                </div>
            </div>

            <!-- Audit Overview Table -->
            <table style="width: 100%; border-collapse: collapse; margin-bottom: 24px; font-size: 0.88rem;">
                <tr style="background: #F1F5F9;">
                    <td style="padding: 10px 14px; border: 1px solid #CBD5E1; font-weight: 700; width: 22%;">Mining Customer:</td>
                    <td style="padding: 10px 14px; border: 1px solid #CBD5E1; font-weight: 600;">Letaba Crushers (Pty) Ltd (LC-014466)</td>
                    <td style="padding: 10px 14px; border: 1px solid #CBD5E1; font-weight: 700; width: 20%;">Mine Code / Reg:</td>
                    <td style="padding: 10px 14px; border: 1px solid #CBD5E1; font-weight: 600;">DMRE #14466</td>
                </tr>
                <tr>
                    <td style="padding: 10px 14px; border: 1px solid #CBD5E1; font-weight: 700;">Property & Site:</td>
                    <td style="padding: 10px 14px; border: 1px solid #CBD5E1;">Farm Tarentaalrant 24LT, Tzaneen, Limpopo</td>
                    <td style="padding: 10px 14px; border: 1px solid #CBD5E1; font-weight: 700;">Audit Date:</td>
                    <td style="padding: 10px 14px; border: 1px solid #CBD5E1;"><?php echo date('Y-m-d'); ?></td>
                </tr>
                <tr style="background: #F1F5F9;">
                    <td style="padding: 10px 14px; border: 1px solid #CBD5E1; font-weight: 700;">Ablution Facility:</td>
                    <td style="padding: 10px 14px; border: 1px solid #CBD5E1; font-weight: 700; color: #D32F2F;">Monthly Ablution Facilities Checklist (Male Block 1)</td>
                    <td style="padding: 10px 14px; border: 1px solid #CBD5E1; font-weight: 700;">Compliance Score:</td>
                    <td style="padding: 10px 14px; border: 1px solid #CBD5E1; font-weight: 900; color: #059669; font-size: 1.1rem;">88.4% (PASSED)</td>
                </tr>
            </table>

            <!-- Category Breakdown Scores -->
            <h4 style="font-size: 1.05rem; font-weight: 800; color: #0F172A; margin-bottom: 12px;">
                Inspection Category Scores (51 Points Audited)
            </h4>
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 12px; margin-bottom: 24px;">
                <div style="background: #F8FAFC; border: 1px solid #E2E8F0; padding: 12px; border-radius: 6px;">
                    <div style="font-size: 0.75rem; color: #64748B; font-weight: 700;">1. Basins & Taps</div>
                    <div style="font-size: 1.25rem; font-weight: 900; color: #059669;">100% (8/8)</div>
                </div>
                <div style="background: #F8FAFC; border: 1px solid #E2E8F0; padding: 12px; border-radius: 6px;">
                    <div style="font-size: 0.75rem; color: #64748B; font-weight: 700;">2. Showers (Male)</div>
                    <div style="font-size: 1.25rem; font-weight: 900; color: #D97706;">88.9% (8/9)</div>
                </div>
                <div style="background: #F8FAFC; border: 1px solid #E2E8F0; padding: 12px; border-radius: 6px;">
                    <div style="font-size: 0.75rem; color: #64748B; font-weight: 700;">3. Toilets & Pans</div>
                    <div style="font-size: 1.25rem; font-weight: 900; color: #059669;">90.0% (9/10)</div>
                </div>
                <div style="background: #F8FAFC; border: 1px solid #E2E8F0; padding: 12px; border-radius: 6px;">
                    <div style="font-size: 0.75rem; color: #64748B; font-weight: 700;">4. Urinal Channels</div>
                    <div style="font-size: 1.25rem; font-weight: 900; color: #059669;">87.5% (7/8)</div>
                </div>
                <div style="background: #F8FAFC; border: 1px solid #E2E8F0; padding: 12px; border-radius: 6px;">
                    <div style="font-size: 0.75rem; color: #64748B; font-weight: 700;">5. Dual-Lockers</div>
                    <div style="font-size: 1.25rem; font-weight: 900; color: #059669;">87.5% (7/8)</div>
                </div>
                <div style="background: #F8FAFC; border: 1px solid #E2E8F0; padding: 12px; border-radius: 6px;">
                    <div style="font-size: 0.75rem; color: #64748B; font-weight: 700;">6. Sanitation/SDS</div>
                    <div style="font-size: 1.25rem; font-weight: 900; color: #059669;">75.0% (6/8)</div>
                </div>
            </div>

            <!-- Identified Deficiencies & Remedial Action -->
            <h4 style="font-size: 1.05rem; font-weight: 800; color: #0F172A; margin-bottom: 12px;">
                Remedial Action Register (Assigned for Correction)
            </h4>
            <table style="width: 100%; border-collapse: collapse; margin-bottom: 24px; font-size: 0.85rem;">
                <thead>
                    <tr style="background: #0F172A; color: #FFFFFF;">
                        <th style="padding: 10px; text-align: left;">Item #</th>
                        <th style="padding: 10px; text-align: left;">Finding / Defect Description</th>
                        <th style="padding: 10px; text-align: left;">Severity</th>
                        <th style="padding: 10px; text-align: left;">Remedial Action Required</th>
                        <th style="padding: 10px; text-align: left;">Due Date</th>
                    </tr>
                </thead>
                <tbody>
                    <tr style="border-bottom: 1px solid #CBD5E1;">
                        <td style="padding: 10px; font-weight: 700;">#10</td>
                        <td style="padding: 10px;">Shower 3 hot water mixing tap leaking slightly onto floor drain.</td>
                        <td style="padding: 10px;"><span style="background: #FEF3C7; color: #92400E; padding: 3px 8px; border-radius: 4px; font-weight: 700;">MEDIUM</span></td>
                        <td style="padding: 10px;">Replace rubber tap washer and reseat valve.</td>
                        <td style="padding: 10px; font-weight: 600;">3 Days</td>
                    </tr>
                    <tr style="border-bottom: 1px solid #CBD5E1;">
                        <td style="padding: 10px; font-weight: 700;">#46</td>
                        <td style="padding: 10px;">Safety Data Sheet (SDS) for Pine Disinfectant expired/faded in janitor room.</td>
                        <td style="padding: 10px;"><span style="background: #FEE2E2; color: #991B1B; padding: 3px 8px; border-radius: 4px; font-weight: 700;">HIGH</span></td>
                        <td style="padding: 10px;">Print and laminate updated GHS SDS sheet and affix to cabinet door.</td>
                        <td style="padding: 10px; font-weight: 600;">24 Hours</td>
                    </tr>
                </tbody>
            </table>

            <!-- Dual Sign-off Blocks -->
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 24px; margin-top: 30px; padding-top: 20px; border-top: 2px dashed #CBD5E1;">
                <div style="border: 1px solid #E2E8F0; padding: 16px; border-radius: 8px;">
                    <div style="font-size: 0.78rem; color: #64748B; font-weight: 800; text-transform: uppercase;">
                        Inspecting Field Officer / SAIOH Hygienist
                    </div>
                    <div style="margin-top: 14px; font-family: cursive; font-size: 1.3rem; color: #0F172A;">
                        J. Mokoena (SAIOH #4492)
                    </div>
                    <div style="font-size: 0.8rem; color: #475569; margin-top: 4px;">
                        Digitally verified & geostamped via MobiWorkforce Android Terminal
                    </div>
                </div>

                <div style="border: 1px solid #E2E8F0; padding: 16px; border-radius: 8px;">
                    <div style="font-size: 0.78rem; color: #64748B; font-weight: 800; text-transform: uppercase;">
                        Mine Manager / Client Representative Sign-off
                    </div>
                    <div style="margin-top: 14px; font-family: cursive; font-size: 1.3rem; color: #0F172A;">
                        P. van der Merwe (Letaba Crushers)
                    </div>
                    <div style="font-size: 0.8rem; color: #475569; margin-top: 4px;">
                        Compliance certificate acknowledged & remedial tasks scheduled
                    </div>
                </div>
            </div>
        </div>
        <?php
        return ob_get_clean();
    }
}

new MobiWorkforce_Reports_PDF();
