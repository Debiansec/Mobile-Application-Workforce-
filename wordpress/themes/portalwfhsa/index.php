<?php
/**
 * Portal WFHSA Main Index Template
 */
get_header(); ?>

<main class="portalwfhsa-container">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;">
        <div>
            <h1 style="font-size: 1.8rem; font-weight: 900; color: #FFFFFF;">Mining Ablution Compliance Portal</h1>
            <p style="color: var(--wfhsa-text-muted); font-size: 0.9rem;">
                Official statutory inspections, 28-page master assessments, and MHSA compliance telemetry.
            </p>
        </div>
        <div>
            <a href="<?php echo esc_url(home_url('/wp-admin/post-new.php?post_type=portalwfhsa_assessment')); ?>" class="portalwfhsa-btn portalwfhsa-btn-primary">
                + Launch Field Inspection
            </a>
        </div>
    </div>

    <!-- Quick Stats Grid -->
    <div class="portalwfhsa-grid">
        <div class="portalwfhsa-stat-card">
            <span class="portalwfhsa-stat-label">Active Inspections</span>
            <span class="portalwfhsa-stat-value">51</span>
            <span class="portalwfhsa-stat-desc">Ablution checklists registered</span>
        </div>
        <div class="portalwfhsa-stat-card">
            <span class="portalwfhsa-stat-label">Average Conformance</span>
            <span class="portalwfhsa-stat-value" style="color: var(--wfhsa-green);">87.5%</span>
            <span class="portalwfhsa-stat-desc">Above 80% MHSA statutory threshold</span>
        </div>
        <div class="portalwfhsa-stat-card">
            <span class="portalwfhsa-stat-label">Remedial Actions</span>
            <span class="portalwfhsa-stat-value" style="color: var(--wfhsa-orange);">3 Open</span>
            <span class="portalwfhsa-stat-desc">Target dates tracked in Section 10</span>
        </div>
        <div class="portalwfhsa-stat-card">
            <span class="portalwfhsa-stat-label">Sync Gateway</span>
            <span class="portalwfhsa-stat-value" style="font-size: 1.2rem; color: var(--wfhsa-blue);">LIVE</span>
            <span class="portalwfhsa-stat-desc">https://wfhsa.com/new/</span>
        </div>
    </div>

    <!-- Live Assessments Table -->
    <div class="portalwfhsa-card">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
            <h2 style="font-size: 1.1rem; font-weight: 800; color: #FFFFFF;">Recent Statutory Inspections</h2>
            <span class="portalwfhsa-badge portalwfhsa-badge-green">Compulsory Remarks Active</span>
        </div>

        <table class="portalwfhsa-table">
            <thead>
                <tr>
                    <th>Audit Reference</th>
                    <th>Customer / Mine Site</th>
                    <th>Ablution Facility</th>
                    <th>Score</th>
                    <th>Status</th>
                    <th>Date</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td><strong>WFH-OH-2026-0042</strong></td>
                    <td>Letaba Crushers (Pty) Ltd — Main Quarry</td>
                    <td>Main Change House — Male</td>
                    <td><span class="portalwfhsa-badge portalwfhsa-badge-green">85% PASS</span></td>
                    <td><span class="portalwfhsa-badge portalwfhsa-badge-orange">UNDER REVIEW</span></td>
                    <td>2026-10-29</td>
                    <td><a href="<?php echo esc_url(home_url('/inspection-report/')); ?>" class="portalwfhsa-btn portalwfhsa-btn-outline" style="padding: 4px 8px; font-size: 0.75rem;">View Dossier</a></td>
                </tr>
                <tr>
                    <td><strong>WFH-AFR-KUI-OD-26-001</strong></td>
                    <td>Afrimat CM North — Kuipersbult Quarry</td>
                    <td>Plant Male Ablution Facility</td>
                    <td><span class="portalwfhsa-badge portalwfhsa-badge-green">91% PASS</span></td>
                    <td><span class="portalwfhsa-badge portalwfhsa-badge-green">APPROVED</span></td>
                    <td>2026-10-28</td>
                    <td><a href="<?php echo esc_url(home_url('/inspection-report/')); ?>" class="portalwfhsa-btn portalwfhsa-btn-outline" style="padding: 4px 8px; font-size: 0.75rem;">View Dossier</a></td>
                </tr>
            </tbody>
        </table>
    </div>
</main>

<?php get_footer(); ?>
