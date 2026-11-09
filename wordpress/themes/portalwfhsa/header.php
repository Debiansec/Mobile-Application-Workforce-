<?php
/**
 * Portal WFHSA Header Template with Elementor support
 */
?><!DOCTYPE html>
<html <?php language_attributes(); ?>>
<head>
    <meta charset="<?php bloginfo('charset'); ?>">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="profile" href="https://gmpg.org/xfn/11">
    <?php wp_head(); ?>
</head>
<body <?php body_class(); ?>>
<?php wp_body_open(); ?>

<?php
if ( ! function_exists('elementor_theme_do_location') || ! elementor_theme_do_location('header') ) :
?>
<header class="portalwfhsa-header">
    <div class="portalwfhsa-brand">
        <div class="portalwfhsa-logo-mark">WFH</div>
        <div>
            <div class="portalwfhsa-title">PORTAL WFHSA</div>
            <div class="portalwfhsa-subtitle">Occupational Hygiene & Ablution Audits</div>
        </div>
    </div>

    <nav class="portalwfhsa-nav">
        <a href="<?php echo esc_url(home_url('/')); ?>" class="portalwfhsa-btn portalwfhsa-btn-outline">Dashboard</a>
        <a href="<?php echo esc_url(home_url('/new-inspection/')); ?>" class="portalwfhsa-btn portalwfhsa-btn-primary">+ New Inspection</a>
        <div class="portalwfhsa-badge portalwfhsa-badge-green">
            <span style="font-size:0.6rem;">●</span> https://wfhsa.com/new/
        </div>
    </nav>
</header>
<?php endif; ?>
