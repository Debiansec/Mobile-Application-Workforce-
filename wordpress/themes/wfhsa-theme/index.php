<?php
/**
 * Main dashboard & home template for WorkForce Health SA
 */
get_header(); ?>

<main class="wfhsa-container">
    <!-- Hero Banner with mining graphics -->
    <div style="position: relative; border-radius: 14px; overflow: hidden; margin-bottom: 28px; border: 1px solid var(--wfhsa-border); min-height: 220px; display: flex; align-items: flex-end; padding: 24px; background: linear-gradient(180deg, rgba(15,16,21,0.2) 0%, rgba(15,16,21,0.92) 85%), url('<?php echo esc_url(get_template_directory_uri() . '/images/hero.png'); ?>') center/cover;">
        <div>
            <div style="font-size: 0.8rem; font-weight: 800; color: var(--wfhsa-orange); letter-spacing: 2px;">
                FIELD AUDIT & COMPLIANCE ENGINE
            </div>
            <h1 style="font-size: 2rem; font-weight: 900; color: #FFFFFF; margin: 4px 0 8px 0;">
                Occupational Hygiene Inspections
            </h1>
            <p style="color: var(--wfhsa-text-muted); font-size: 0.95rem; max-width: 650px;">
                Initial Module: Monthly Ablution Facilities Checklist (Male). Seamless mobile & desktop data capture with direct cloud synchronization at <strong>https://wfhsa.com</strong>.
            </p>
        </div>
    </div>

    <!-- Active Shortcodes or Page Content -->
    <?php if (have_posts()) : while (have_posts()) : the_post(); ?>
        <?php the_content(); ?>
    <?php endwhile; else: ?>
        <!-- Default Hub Widgets if no specific page content -->
        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); gap: 20px;">
            <div class="wfhsa-card">
                <h3 style="font-size: 1.15rem; margin-bottom: 8px; color: #FFFFFF;">
                    1. Select Authorized Customer & Site
                </h3>
                <p style="color: var(--wfhsa-text-muted); font-size: 0.85rem; margin-bottom: 16px;">
                    Choose an authorized mining client, property, and facility to start or resume an inspection.
                </p>
                <?php echo do_shortcode('[wfhsa_location_selector]'); ?>
            </div>

            <div class="wfhsa-card">
                <h3 style="font-size: 1.15rem; margin-bottom: 8px; color: #FFFFFF;">
                    2. Authorized User & Role Switcher
                </h3>
                <p style="color: var(--wfhsa-text-muted); font-size: 0.85rem; margin-bottom: 16px;">
                    Enforces role-based permissions (Assessor, Hygienist, Mine Manager, Admin).
                </p>
                <?php echo do_shortcode('[wfhsa_role_switcher]'); ?>
            </div>
        </div>
    <?php endif; ?>
</main>

<?php get_footer(); ?>
