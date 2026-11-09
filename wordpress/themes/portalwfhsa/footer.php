<?php
/**
 * Portal WFHSA Footer Template with Elementor support
 */

if ( ! function_exists('elementor_theme_do_location') || ! elementor_theme_do_location('footer') ) :
?>
<footer class="portalwfhsa-footer">
    <div>
        <strong>Work Force Health (Pty) Ltd</strong> • Reg No. 2017/361830/07 • Mine Health & Safety Act (Act 29 of 1996)
    </div>
    <div>
        Synchronized with Android Companion Inspection App via <code>https://wfhsa.com/new/wp-json/portalwfhsa/v1/sync</code>
    </div>
</footer>
<?php endif; ?>

<?php wp_footer(); ?>
</body>
</html>
