package com.example.data.local

import com.example.data.model.ChecklistType
import com.example.data.model.Customer
import com.example.data.model.Facility
import com.example.data.model.Site
import com.example.data.model.Equipment
import com.example.data.model.EquipmentStatus

object CustomerSeedData {

    val customers = listOf(
        Customer(
            id = "cust_afrimat",
            name = "Afrimat CM North (Pty) Ltd",
            tradeName = "Afrimat CM North (Pty) Ltd",
            companyCode = "AFR-KUI-2026",
            registrationNumber = "2002 / 025895 / 07",
            vatNumber = "4910205428",
            physicalAddress = "3 Sennett Street, Worcester, Western Cape, 6850",
            postalAddress = "P O Box 505, Worcester, Western Cape, 6849",
            controlGroupName = "Afrimat Ltd",
            controlGroupRegNumber = "2006 / 022534 / 06",
            controlGroupVatNumber = "4200236356",
            contactPerson = "Mr Casper Reyneke",
            email = "Casper.reyneke@afrimat.co.za",
            phone = "+27 (0)78 893 2725",
            ceoName = "Mr A J Van Heerden",
            ceoContact = "+27 (0)21 917 8840",
            ceoEmail = "andries@afrimat.co.za",
            employerRepName = "Mr C Reyneke",
            employerRepPhone = "+27 (0)12 664 5649 | +27 (0)78 893 2725",
            employerRepEmail = "Casper.reyneke@afrimat.co.za",
            accountsContactName = "Reinhard Stander",
            accountsContactPhone = "+27 (0)21 917 8840",
            accountsContactEmail = "reinhard.stander@afrimat.co.za"
        ),
        Customer(
            id = "cust_letaba",
            name = "Letaba Crushers (Pty) Ltd",
            tradeName = "Letaba Crushers",
            companyCode = "LC-014466",
            registrationNumber = "1998 / 014466 / 07",
            vatNumber = "4120158941",
            physicalAddress = "Farm Tarentaalrant 24LT, Tzaneen, Limpopo, 0850",
            postalAddress = "P O Box 188, Tzaneen, Limpopo, 0850",
            controlGroupName = "Letaba Holdings",
            controlGroupRegNumber = "1994 / 003412 / 06",
            controlGroupVatNumber = "4010112984",
            contactPerson = "Johan van der Merwe",
            email = "johan.vdm@letabacrushers.co.za",
            phone = "+27 (0)15 307 4100",
            ceoName = "Danie van der Merwe",
            ceoContact = "+27 (0)15 307 4100",
            ceoEmail = "danie@letabacrushers.co.za",
            employerRepName = "Johan van der Merwe",
            employerRepPhone = "+27 (0)82 555 1290",
            employerRepEmail = "johan.vdm@letabacrushers.co.za",
            accountsContactName = "Susan Venter",
            accountsContactPhone = "+27 (0)15 307 4105",
            accountsContactEmail = "accounts@letabacrushers.co.za"
        ),
        Customer(
            id = "cust_marula",
            name = "Marula Platinum Mine (Pty) Ltd",
            tradeName = "Marula Platinum",
            companyCode = "MPM-099201",
            registrationNumber = "2001 / 019283 / 07",
            vatNumber = "4320199201",
            physicalAddress = "Farm Driekop 253KT, Burgersfort, Limpopo, 1150",
            postalAddress = "Private Bag X1010, Steelpoort, 1133",
            controlGroupName = "Implats Group",
            contactPerson = "Thabo Mokoena",
            email = "t.mokoena@marulaplat.co.za",
            phone = "+27 (0)13 230 6000"
        ),
        Customer(
            id = "cust_phalaborwa",
            name = "Palabora Copper Mining Company",
            tradeName = "Palabora Copper",
            companyCode = "PMC-004312",
            registrationNumber = "1956 / 002134 / 06",
            vatNumber = "4010043120",
            physicalAddress = "1 Copper Road, Phalaborwa, Limpopo, 1390",
            postalAddress = "P O Box 65, Phalaborwa, 1390",
            contactPerson = "Ndivhuwo Nemukula",
            email = "n.nemukula@palabora.co.za",
            phone = "+27 (0)15 780 2911"
        )
    )

    val sites = listOf(
        Site(
            id = "site_afrimat_kuipersbult",
            customerId = "cust_afrimat",
            siteName = "Kuipersbult Quarry & Ready-Mix",
            propertyDescription = "Remainder of the farm Kuipersbults 511LQ",
            location = "Lephalale",
            province = "Limpopo Province",
            magisterialDistrict = "Lephalale",
            mineCode = "10023",
            gpsCoordinates = "-23.70760° S, 27.57939° E",
            latitude = -23.70760,
            longitude = 27.57939,
            commoditiesProduced = "Various sized aggregate and ready-mix concrete",
            miningRightNumber = "LP30/5/1/2/2/10023MR",
            waterLicenseNumber = "01/A42J/AGI/4378",
            mineManager = "Mr P K Lekgeu",
            mineManagerContact = "+27 (0)14 763 2100 | kuipersbult@afrimat.co.za",
            siteOccupationalHygienist = "N B Letlape (SAIOH ROH)",
            hygieneAssistant = "N B Ngobese",
            occupationalMedicalPractitioner = "Dr K O Sebopa",
            safetyOfficer = "T. M. Sithole",
            residentEngineer = "J. H. Du Plessis (GCC)",
            totalWorkforce = 185
        ),
        Site(
            id = "site_letaba_tzaneen",
            customerId = "cust_letaba",
            siteName = "Letaba Crushers - Main Quarry & Plant",
            propertyDescription = "Farm Tarentaalrant 24LT",
            location = "Tzaneen",
            province = "Limpopo Province",
            magisterialDistrict = "Greater Tzaneen",
            mineCode = "14466",
            gpsCoordinates = "-23.83120° S, 30.16340° E",
            latitude = -23.83120,
            longitude = 30.16340,
            commoditiesProduced = "Crushed dolerite aggregate & road build material",
            miningRightNumber = "LP30/5/1/2/2/14466MR",
            waterLicenseNumber = "02/B81F/C/1094",
            mineManager = "Pieter Botha (Pr.Eng, GCC)",
            mineManagerContact = "p.botha@letabacrushers.co.za | +27 82 555 1290",
            siteOccupationalHygienist = "Riaan Steyn (SAIOH ROH 0482)",
            hygieneAssistant = "Nomvula Khumalo",
            occupationalMedicalPractitioner = "Dr. M. K. Pillay (MBChB, DOH)",
            safetyOfficer = "Kobus Nel",
            residentEngineer = "Pieter Botha (GCC)",
            totalWorkforce = 145
        ),
        Site(
            id = "site_marula_shaft1",
            customerId = "cust_marula",
            siteName = "Driekop Shaft & Concentrator",
            propertyDescription = "Farm Driekop 253KT",
            location = "Burgersfort / Steelpoort",
            province = "Limpopo Province",
            magisterialDistrict = "Fetakgomo Tubatse",
            mineCode = "09920",
            gpsCoordinates = "-24.52180° S, 30.08920° E",
            latitude = -24.52180,
            longitude = 30.08920,
            commoditiesProduced = "Platinum Group Metals (PGM) ore",
            miningRightNumber = "LP30/5/1/2/2/09920MR",
            waterLicenseNumber = "03/B71D/AB/9841",
            mineManager = "Sipho Khumalo",
            mineManagerContact = "s.khumalo@marulaplat.co.za",
            siteOccupationalHygienist = "Nomvula Dlamini (SAIOH ROH)",
            hygieneAssistant = "M. D. Sithole",
            occupationalMedicalPractitioner = "Dr. C. Van Zyl",
            totalWorkforce = 850
        )
    )

    val facilities = listOf(
        Facility(
            id = "fac_kui_male_ablution",
            siteId = "site_afrimat_kuipersbult",
            name = "Kuipersbult Quarry Main Ablution (Male)",
            checklistType = ChecklistType.MALE_ABLUTION,
            buildingDescription = "Primary Crusher and Haul Truck Workshop Complex",
            estimatedUsers = 110,
            lastAssessmentDate = "2026-09-29"
        ),
        Facility(
            id = "fac_kui_female_ablution",
            siteId = "site_afrimat_kuipersbult",
            name = "Kuipersbult Admin Complex Ablution (Female)",
            checklistType = ChecklistType.FEMALE_ABLUTION,
            buildingDescription = "Weighbridge, Administration & Laboratory Office Block",
            estimatedUsers = 28,
            lastAssessmentDate = "2026-09-29"
        ),
        Facility(
            id = "fac_kui_readymix_toilet",
            siteId = "site_afrimat_kuipersbult",
            name = "Kuipersbult Ready-Mix Plant Unit",
            checklistType = ChecklistType.TOILET_BASIN,
            buildingDescription = "Ready-mix batching plant driver change enclosure",
            estimatedUsers = 35,
            lastAssessmentDate = "2026-09-29"
        ),
        Facility(
            id = "fac_letaba_male_ch",
            siteId = "site_letaba_tzaneen",
            name = "Main Change House — Male",
            checklistType = ChecklistType.MALE_ABLUTION,
            buildingDescription = "Brick building adjacent to Lamp Room, Plant South Gate",
            estimatedUsers = 145,
            lastAssessmentDate = "2026-09-18"
        ),
        Facility(
            id = "fac_letaba_female_ch",
            siteId = "site_letaba_tzaneen",
            name = "Main Change House — Female",
            checklistType = ChecklistType.FEMALE_ABLUTION,
            buildingDescription = "Administration complex annex, East Block",
            estimatedUsers = 38,
            lastAssessmentDate = "2026-09-18"
        ),
        Facility(
            id = "fac_letaba_workshop_ablution",
            siteId = "site_letaba_tzaneen",
            name = "TMM Heavy Equipment Workshop Ablution",
            checklistType = ChecklistType.TOILET_BASIN,
            buildingDescription = "Workshop Bay 4 modular unit",
            estimatedUsers = 25,
            lastAssessmentDate = "2026-09-20"
        )
    )

    val defaultEquipment = listOf(
        Equipment(
            id = "eq_wbgt_01",
            name = "Heat Stress Monitor / WBGT Logger",
            manufacturer = "AZ Instrument Corp",
            model = "AZ 87786 Wearable WBGT Checker",
            serialNumber = "AZ87786-2025-9912",
            assetTag = "WFH-ENV-042",
            lastCalibrationDate = "2026-04-12",
            calibrationDueDate = "2027-04-12",
            status = EquipmentStatus.CALIBRATED,
            certificateRef = "SANAS-CAL-2026-8819"
        ),
        Equipment(
            id = "eq_kestrel_01",
            name = "Environmental Airflow & Weather Meter",
            manufacturer = "Nielsen-Kellerman",
            model = "Kestrel 4000 Pocket Weather Tracker",
            serialNumber = "K4000-843192",
            assetTag = "WFH-AIR-018",
            lastCalibrationDate = "2026-02-10",
            calibrationDueDate = "2027-02-10",
            status = EquipmentStatus.CALIBRATED,
            certificateRef = "SANAS-CAL-2026-4401"
        ),
        Equipment(
            id = "eq_sound_01",
            name = "Type 1 Sound Level Meter & Octave Band Analyzer",
            manufacturer = "Cirrus Research",
            model = "Optimus Red CR:161C",
            serialNumber = "CR161C-G071221",
            assetTag = "WFH-NOISE-009",
            lastCalibrationDate = "2025-11-05",
            calibrationDueDate = "2026-11-05",
            status = EquipmentStatus.CALIBRATION_DUE,
            certificateRef = "SANAS-CAL-2025-1192"
        )
    )
}
