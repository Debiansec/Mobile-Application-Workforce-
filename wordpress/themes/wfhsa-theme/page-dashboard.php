<?php
/**
 * Template Name: WFHSA Dashboard Page
 * Description: Front-end management dashboard for WorkForce Health SA
 */
get_header(); ?>

<main class="wfhsa-container">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;">
        <div>
            <div style="font-size: 0.8rem; font-weight: 800; color: var(--wfhsa-orange); letter-spacing: 1.5px;">
                CENTRAL HYGIENE CONTROL
            </div>
            <h1 style="font-size: 2rem; font-weight: 900; color: #FFFFFF; margin-top: 4px;">
                Inspection Hub & Field Sync
            </h1>
        </div>
        <div style="display: flex; gap: 10px;">
            <a href="<?php echo esc_url(home_url('/?wfhsa_action=new')); ?>" class="wfhsa-btn wfhsa-btn-primary">
                + Launch Inspection
            </a>
        </div>
    </div>

    <!-- 3-Step Location Wizard Component -->
    <div class="wfhsa-card" style="margin-bottom: 24px;">
        <?php echo do_shortcode('[wfhsa_location_selector]'); ?>
    </div>

    <!-- Active Inspection Checklist Wizard -->
    <div class="wfhsa-card" style="margin-bottom: 24px;">
        <?php echo do_shortcode('[wfhsa_new_inspection]'); ?>
    </div>

    <!-- Role Switcher & Security Verification -->
    <div class="wfhsa-card">
        <?php echo do_shortcode('[wfhsa_role_switcher]'); ?>
    </div>
</main>

<?php get_footer(); ?>
