<?php
/**
 * Single Template for Occupational Hygiene Assessments (wfhsa_assessment)
 * Theme: WorkForce Health SA
 */
get_header(); ?>

<main class="wfhsa-container">
    <div style="margin-bottom: 20px;">
        <a href="<?php echo esc_url(home_url('/')); ?>" style="color: var(--wfhsa-orange); font-size: 0.9rem; font-weight: 700;">
            &larr; Back to Platform Home
        </a>
    </div>

    <?php while (have_posts()) : the_post(); 
        $post_id = get_the_ID();
        $ref = get_the_title();
        $score = get_post_meta($post_id, 'wfhsa_score', true);
        if ($score === '') $score = 85;
        $customer = get_post_meta($post_id, 'wfhsa_customer', true);
        if (!$customer) $customer = 'Letaba Crushers (Pty) Ltd';
        $site = get_post_meta($post_id, 'wfhsa_site', true);
        if (!$site) $site = 'Farm Tarentaalrant 24LT, Tzaneen, Limpopo (Mine #14466)';
        $facility = get_post_meta($post_id, 'wfhsa_facility', true);
        if (!$facility) $facility = 'Monthly Ablution Facilities Checklist — Male (Main Change House)';
        $status = get_post_meta($post_id, 'wfhsa_status', true);
        if (!$status) $status = 'SUBMITTED_FOR_REVIEW';
    ?>

    <article class="wfhsa-card" style="padding: 0; overflow: hidden; background: #ffffff; color: #1e293b;">
        <div style="background: #171922; color: #ffffff; padding: 20px 24px; display: flex; justify-content: space-between; align-items: center; border-bottom: 3px solid #D32F2F;">
            <div>
                <span class="wfhsa-badge" style="background: rgba(255, 109, 0, 0.2); color: #FF6D00; border: 1px solid #FF6D00;">
                    ASSESSMENT AUDIT RECORD
                </span>
                <h1 style="font-size: 1.5rem; margin: 8px 0 2px 0; color: #ffffff; font-weight: 900;">
                    <?php echo esc_html($ref); ?>
                </h1>
                <div style="font-size: 0.85rem; color: #94A3B8;">
                    <?php echo esc_html($customer); ?> &bull; <?php echo esc_html($facility); ?>
                </div>
            </div>
            <div style="text-align: right;">
                <button onclick="window.print()" class="wfhsa-btn wfhsa-btn-primary" style="min-height: 40px; padding: 8px 18px; font-size: 0.85rem;">
                    🖨️ Print / Save PDF
                </button>
            </div>
        </div>

        <div style="padding: 30px;">
            <?php 
            if (class_exists('WFHSA_Reports_PDF')) {
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
