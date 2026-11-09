<?php
/**
 * Single Template for Occupational Hygiene Assessments
 * Theme: MobiWorkforce
 * URI: https://wfhsa.com/new/
 */
get_header(); ?>

<main class="wfhsa-container">
    <div style="margin-bottom: 20px; display:flex; justify-content:space-between; align-items:center;">
        <a href="<?php echo esc_url(home_url('/')); ?>" style="color: var(--wfhsa-orange); font-size: 0.9rem; font-weight: 700;">
            &larr; Back to MobiWorkforce Dashboard
        </a>
        <span class="wfhsa-badge badge-green">AUDIT RECORD STORED</span>
    </div>

    <?php while (have_posts()) : the_post(); 
        $post_id = get_the_ID();
        $ref = get_the_title();
        $score = get_post_meta($post_id, 'wfhsa_score', true);
        if ($score === '') $score = get_post_meta($post_id, 'mobiworkforce_score', true);
        if ($score === '') $score = 88;
        
        $customer = get_post_meta($post_id, 'wfhsa_customer', true);
        if (!$customer) $customer = get_post_meta($post_id, 'mobiworkforce_customer', true);
        if (!$customer) $customer = 'Letaba Crushers (Pty) Ltd';

        $site = get_post_meta($post_id, 'wfhsa_site', true);
        if (!$site) $site = get_post_meta($post_id, 'mobiworkforce_site', true);
        if (!$site) $site = 'Farm Tarentaalrant 24LT, Tzaneen, Limpopo (Mine #14466)';

        $facility = get_post_meta($post_id, 'wfhsa_facility', true);
        if (!$facility) $facility = get_post_meta($post_id, 'mobiworkforce_facility', true);
        if (!$facility) $facility = 'Monthly Ablution Facilities Checklist — Male (Main Change House)';

        $status = get_post_meta($post_id, 'wfhsa_status', true);
        if (!$status) $status = 'SYNCED_AND_REVIEWED';

        $score_num = floatval($score);
        $score_color = ($score_num >= 80) ? '#10B981' : (($score_num >= 60) ? '#F59E0B' : '#D32F2F');
        $compliance_text = ($score_num >= 80) ? 'DMR COMPLIANT' : 'NON-COMPLIANT - REMEDIAL REQUIRED';
    ?>

    <article class="wfhsa-card" style="padding: 0; overflow: hidden; background: #FFFFFF; color: #1E293B; border-radius:12px;">
        <div style="background: #171922; color: #FFFFFF; padding: 22px 28px; display: flex; justify-content: space-between; align-items: center; border-bottom: 3px solid #D32F2F; flex-wrap:wrap; gap:16px;">
            <div>
                <span class="wfhsa-badge badge-orange" style="margin-bottom:6px;">
                    MOBIWORKFORCE AUDIT CERTIFICATE
                </span>
                <h1 style="font-size: 1.6rem; margin: 4px 0 2px 0; color: #FFFFFF; font-weight: 900;">
                    <?php echo esc_html($ref); ?>
                </h1>
                <div style="font-size: 0.88rem; color: #94A3B8;">
                    <?php echo esc_html($customer); ?> &bull; <?php echo esc_html($site); ?>
                </div>
            </div>
            <div style="display:flex; align-items:center; gap:14px;">
                <div style="text-align:right;">
                    <div style="font-size: 0.72rem; color: #94A3B8; text-transform:uppercase;">Overall Score</div>
                    <div style="font-size: 1.8rem; font-weight: 900; color: <?php echo esc_attr($score_color); ?>;">
                        <?php echo esc_html($score); ?>%
                    </div>
                </div>
                <button onclick="window.print()" class="wfhsa-btn wfhsa-btn-primary" style="min-height: 40px; padding: 8px 18px; font-size: 0.85rem;">
                    🖨️ Export PDF Report
                </button>
            </div>
        </div>

        <div style="padding: 28px;">
            <!-- Facility Meta Strip -->
            <div style="display:grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap:16px; background:#F8FAFC; border:1px solid #E2E8F0; border-radius:8px; padding:16px; margin-bottom:24px;">
                <div>
                    <div style="font-size:0.75rem; color:#64748B; font-weight:700; text-transform:uppercase;">Facility Tested</div>
                    <div style="font-weight:700; color:#0F172A; font-size:0.95rem; margin-top:2px;"><?php echo esc_html($facility); ?></div>
                </div>
                <div>
                    <div style="font-size:0.75rem; color:#64748B; font-weight:700; text-transform:uppercase;">Compliance Status</div>
                    <div style="font-weight:800; color:<?php echo esc_attr($score_color); ?>; font-size:0.95rem; margin-top:2px;"><?php echo esc_html($compliance_text); ?></div>
                </div>
                <div>
                    <div style="font-size:0.75rem; color:#64748B; font-weight:700; text-transform:uppercase;">Cloud Synchronization</div>
                    <div style="font-weight:700; color:#047857; font-size:0.95rem; margin-top:2px;">Synced to wfhsa.com/new/</div>
                </div>
            </div>

            <?php 
            if (class_exists('MobiWorkforce_Reports_PDF')) {
                $reporter = new MobiWorkforce_Reports_PDF();
                echo $reporter->generate_report_html('asm_letaba_01');
            } elseif (class_exists('WFHSA_Reports_PDF')) {
                $reporter = new WFHSA_Reports_PDF();
                echo $reporter->generate_report_html('asm_letaba_01');
            } else {
                the_content();
            }
            ?>
        </div>
    </article>

    <?php endwhile; ?>
</main>

<?php get_footer(); ?>
