<?php
/**
 * Single Inspection Dossier Viewer (28-Page Master Template Layout)
 */
get_header(); ?>

<main class="portalwfhsa-container">
    <div class="portalwfhsa-card" style="border-top: 4px solid var(--wfhsa-red);">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
            <div>
                <span class="portalwfhsa-badge portalwfhsa-badge-red" style="margin-bottom: 6px;">DOCUMENT NUMBER: – PART 1</span>
                <h1 style="font-size: 1.6rem; font-weight: 900; color: #FFFFFF;">
                    Occupational Hygiene Assessment Work Instruction Report: – Ablution Facilities
                </h1>
                <p style="color: var(--wfhsa-orange); font-size: 0.85rem; font-weight: 700;">
                    South African Mine Health & Safety Act (MHSA) Compliance Dossier • 28 Pages
                </p>
            </div>
            <div>
                <a href="<?php echo esc_url(rest_url('portalwfhsa/v1/inspections/pdf?id=' . get_the_ID())); ?>" class="portalwfhsa-btn portalwfhsa-btn-primary" target="_blank">
                    Download 28-Page PDF
                </a>
            </div>
        </div>

        <div class="portalwfhsa-grid">
            <div class="portalwfhsa-stat-card">
                <span class="portalwfhsa-stat-label">Customer / Site</span>
                <span class="portalwfhsa-stat-value" style="font-size: 1.1rem;">Letaba Crushers (Pty) Ltd</span>
                <span class="portalwfhsa-stat-desc">DME #7721 • Main Quarry & Plant</span>
            </div>
            <div class="portalwfhsa-stat-card">
                <span class="portalwfhsa-stat-label">Overall Conformance Score</span>
                <span class="portalwfhsa-stat-value" style="color: var(--wfhsa-green);">85%</span>
                <span class="portalwfhsa-stat-desc">DMR PASS • SANS 10400 Compliant</span>
            </div>
            <div class="portalwfhsa-stat-card">
                <span class="portalwfhsa-stat-label">Appointed Hygienist & Assessor</span>
                <span class="portalwfhsa-stat-value" style="font-size: 1rem;">Dr. H. Venter / K. Nkosi</span>
                <span class="portalwfhsa-stat-desc">SAIOH ROH 0482 / OHA-09941</span>
            </div>
            <div class="portalwfhsa-stat-card">
                <span class="portalwfhsa-stat-label">Compulsory Remarks</span>
                <span class="portalwfhsa-stat-value" style="color: var(--wfhsa-green); font-size: 1.1rem;">100% CAPTURED</span>
                <span class="portalwfhsa-stat-desc">Voice Dictation & Geotags Verified</span>
            </div>
        </div>

        <div style="background: var(--wfhsa-surface-variant); padding: 16px; border-radius: var(--radius-sm); margin-top: 16px;">
            <h3 style="font-size: 0.95rem; font-weight: 800; color: #FFFFFF; margin-bottom: 8px;">Master Template Structure (Pages 1 to 28)</h3>
            <p style="font-size: 0.8rem; color: var(--wfhsa-text-muted); line-height: 1.6;">
                Cover (P1) • Section A Remedial Action from Previous Visit (P2) • Section B Special Hygienist Instructions (P3) • Section 1 Customer Info (P4) • Section 2 Environmental 3-Shift Table & WBGT (P5) • Section 3 Executive Summary Notice (P6) • Section 4 Table of Contents (P7) • Section 5 Abbreviations & Terms (P8) • Section 6 Introduction (P9-11) • Section 7 Methodology & Focus Areas (P12-13) • Section 8 Checklists (P14-25) • Section 9 Field Officer Comments (P26) • Section 10 Remedial Actions Table (P27) • Section 11 Confirmation of Receipt & 5 Digital Signatures (P28).
            </p>
        </div>
    </div>
</main>

<?php get_footer(); ?>
