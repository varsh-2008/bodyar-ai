package com.example.data

import com.example.model.AnatomySubgroup
import com.example.model.BodyRegionId
import com.example.model.BodyRegionInfo
import com.example.ui.theme.BioluminescentTeal
import com.example.ui.theme.CobaltBlue
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.SkyBlue

object AnatomyCatalog {

    val regionOrder: List<BodyRegionId> = listOf(
        BodyRegionId.HEAD,
        BodyRegionId.SHOULDERS,
        BodyRegionId.CHEST,
        BodyRegionId.ARMS,
        BodyRegionId.HANDS,
        BodyRegionId.ABDOMEN,
        BodyRegionId.LEGS,
        BodyRegionId.FEET,
    )

    val regions: Map<BodyRegionId, BodyRegionInfo> = mapOf(
        BodyRegionId.HEAD to BodyRegionInfo(
            id = BodyRegionId.HEAD,
            name = "Head",
            clinicalName = "Cranium & Facial Region",
            shortLabel = "HEAD",
            summary = "Houses the brain, primary sensory organs, and upper airway and digestive entrances within a protective bony vault.",
            associatedAnatomy = listOf(
                AnatomySubgroup(
                    category = "Bones",
                    items = listOf(
                        "Cranium (Frontal, Parietal, Temporal, Occipital)",
                        "Mandible",
                        "Maxilla",
                        "Zygomatic Bones",
                    ),
                ),
                AnatomySubgroup(
                    category = "Organs & Structures",
                    items = listOf(
                        "Brain (Cerebrum, Cerebellum, Brainstem)",
                        "Eyes & Optic Nerves",
                        "Inner & Outer Ears",
                        "Paranasal Sinuses",
                    ),
                ),
                AnatomySubgroup(
                    category = "Muscles",
                    items = listOf("Masseter", "Temporalis", "Orbicularis Oculi", "Frontalis"),
                ),
            ),
            functions = listOf(
                "Coordinates cognition, memory, emotion, and voluntary motor control via the brain.",
                "Processes specialized senses: vision, hearing, vestibular balance, smell, and taste.",
                "Protects delicate neural tissue inside the rigid cranial vault.",
                "Enables speech, facial expression, chewing, and initial respiration.",
            ),
            interestingFacts = listOf(
                "The brain consumes roughly 20% of the body's oxygen and calories despite making up only ~2% of body weight.",
                "The stapes bone in the middle ear is the smallest bone in the human body—about the size of a grain of rice.",
                "Your skull is composed of 22 bones joined by fibrous sutures (except the movable mandible).",
            ),
            relatedSystems = listOf("Nervous System", "Skeletal System", "Sensory System", "Muscular System"),
            accentColor = ElectricCyan,
        ),
        BodyRegionId.SHOULDERS to BodyRegionInfo(
            id = BodyRegionId.SHOULDERS,
            name = "Shoulders",
            clinicalName = "Pectoral Girdle & Glenohumeral Complex",
            shortLabel = "SHOULDERS",
            summary = "The most mobile joint complex in the human body, connecting the upper limbs to the axial skeleton.",
            associatedAnatomy = listOf(
                AnatomySubgroup(
                    category = "Bones",
                    items = listOf("Clavicle (Collarbone)", "Scapula (Shoulder Blade)", "Proximal Humerus"),
                ),
                AnatomySubgroup(
                    category = "Muscles",
                    items = listOf(
                        "Deltoid (Anterior, Lateral, Posterior)",
                        "Rotator Cuff (Supraspinatus, Infraspinatus, Teres Minor, Subscapularis)",
                        "Upper Trapezius & Serratus Anterior",
                    ),
                ),
                AnatomySubgroup(
                    category = "Joints & Tissue",
                    items = listOf("Glenohumeral Joint", "Acromioclavicular (AC) Joint", "Glenoid Labrum", "Bursae"),
                ),
            ),
            functions = listOf(
                "Provides 3-axis range of motion—flexion, extension, abduction, adduction, and rotation.",
                "Suspends and stabilizes the arms while transferring mechanical loads to the rib cage.",
                "Rotator cuff muscles dynamically center the humeral head inside the shallow glenoid socket.",
            ),
            interestingFacts = listOf(
                "The shoulder trades bony stability for extreme mobility—its joint resembles a golf ball resting on a tee.",
                "The clavicle is the most frequently fractured bone and the last bone to complete ossification (around age 25).",
                "Four rotator cuff muscles synchronize every time you raise your arm overhead.",
            ),
            relatedSystems = listOf("Muscular System", "Skeletal System", "Nervous System (Brachial Plexus)"),
            accentColor = SkyBlue,
        ),
        BodyRegionId.CHEST to BodyRegionInfo(
            id = BodyRegionId.CHEST,
            name = "Chest",
            clinicalName = "Thoracic Cavity & Pectoral Wall",
            shortLabel = "CHEST",
            summary = "A dynamic bony cage safeguarding the heart, lungs, and major blood vessels while powering respiration.",
            associatedAnatomy = listOf(
                AnatomySubgroup(
                    category = "Bones",
                    items = listOf("Sternum (Breastbone)", "12 Pairs of Ribs", "Thoracic Vertebrae (T1–T12)"),
                ),
                AnatomySubgroup(
                    category = "Vital Organs",
                    items = listOf("Heart & Pericardium", "Right & Left Lungs", "Trachea & Bronchi", "Esophagus & Aorta"),
                ),
                AnatomySubgroup(
                    category = "Muscles",
                    items = listOf("Pectoralis Major & Minor", "Intercostal Muscles", "Thoracic Diaphragm"),
                ),
            ),
            functions = listOf(
                "Pumps oxygenated and deoxygenated blood continuously across systemic and pulmonary circuits.",
                "Expands and contracts via rib elevation and diaphragm descent to draw air into lung alveoli.",
                "Shields vital cardiopulmonary organs from external impact.",
            ),
            interestingFacts = listOf(
                "Your heart beats roughly 100,000 times per day, pumping around 7,500 liters of blood.",
                "The right lung has 3 lobes, while the left lung has 2 lobes and a cardiac notch to leave room for the heart.",
                "The lowest two pairs of ribs (11 and 12) are called floating ribs because they do not attach to the sternum.",
            ),
            relatedSystems = listOf("Cardiovascular System", "Respiratory System", "Skeletal System", "Muscular System"),
            accentColor = ElectricCyan,
        ),
        BodyRegionId.ARMS to BodyRegionInfo(
            id = BodyRegionId.ARMS,
            name = "Arms",
            clinicalName = "Brachium & Antebrachium",
            shortLabel = "ARMS",
            summary = "Multi-lever upper limb segments engineered for reach, lifting force, and rapid forearm rotation.",
            associatedAnatomy = listOf(
                AnatomySubgroup(
                    category = "Bones",
                    items = listOf("Humerus (Upper Arm)", "Radius (Lateral Forearm)", "Ulna (Medial Forearm)"),
                ),
                AnatomySubgroup(
                    category = "Muscles",
                    items = listOf("Biceps Brachii", "Triceps Brachii", "Brachialis", "Brachioradialis", "Pronator Teres"),
                ),
                AnatomySubgroup(
                    category = "Neurovascular",
                    items = listOf("Brachial, Radial & Ulnar Arteries", "Median, Ulnar & Radial Nerves"),
                ),
            ),
            functions = listOf(
                "Generates flexion and extension torque at the elbow hinge joint.",
                "Rotates the palm upward (supination) and downward (pronation) as the radius pivots over the ulna.",
                "Positions the hands accurately in 3D space for precision manipulation and strength tasks.",
            ),
            interestingFacts = listOf(
                "The 'funny bone' isn't a bone—it's the ulnar nerve passing just behind the medial epicondyle of the humerus.",
                "The triceps brachii accounts for roughly two-thirds of the upper arm's muscle mass.",
                "Your forearm contains 20 distinct muscles that act via long tendons to control your wrist and fingers.",
            ),
            relatedSystems = listOf("Muscular System", "Skeletal System", "Cardiovascular System", "Nervous System"),
            accentColor = CobaltBlue,
        ),
        BodyRegionId.HANDS to BodyRegionInfo(
            id = BodyRegionId.HANDS,
            name = "Hands",
            clinicalName = "Carpus, Metacarpus & Digits",
            shortLabel = "HANDS",
            summary = "High-density biomechanical instruments capable of both micro-millimeter precision and powerful grip.",
            associatedAnatomy = listOf(
                AnatomySubgroup(
                    category = "Bones (27 per hand)",
                    items = listOf("8 Carpals (Wrist)", "5 Metacarpals (Palm)", "14 Phalanges (Fingers & Thumb)"),
                ),
                AnatomySubgroup(
                    category = "Muscles & Tendons",
                    items = listOf("Thenar & Hypothenar Eminences", "Lumbricals", "Interossei", "Flexor & Extensor Tendons"),
                ),
                AnatomySubgroup(
                    category = "Sensory Receptors",
                    items = listOf("Meissner's & Pacinian Corpuscles", "Digital Nerves"),
                ),
            ),
            functions = listOf(
                "Executes opposable thumb pinch grips for tool use, writing, and fine motor control.",
                "Provides ultra-high tactile feedback—fingertips detect microscopic surface textures.",
                "Distributes force evenly through palmar arches during grasping, pushing, and climbing.",
            ),
            interestingFacts = listOf(
                "Over a quarter of all bones in the human body (54 out of 206) are located in your two hands.",
                "Fingers have no muscles inside them—they move via tendons connected to muscles in the forearm.",
                "A massive share of the brain's motor and somatosensory cortex is dedicated solely to the hands.",
            ),
            relatedSystems = listOf("Skeletal System", "Muscular System", "Nervous System", "Integumentary System"),
            accentColor = BioluminescentTeal,
        ),
        BodyRegionId.ABDOMEN to BodyRegionInfo(
            id = BodyRegionId.ABDOMEN,
            name = "Abdomen",
            clinicalName = "Abdominal Cavity & Core Region",
            shortLabel = "ABDOMEN",
            summary = "The metabolic and stabilizing center of the body, housing digestive organs and deep core musculature.",
            associatedAnatomy = listOf(
                AnatomySubgroup(
                    category = "Organs",
                    items = listOf(
                        "Stomach",
                        "Liver & Gallbladder",
                        "Small & Large Intestines",
                        "Pancreas & Spleen",
                        "Kidneys (Retroperitoneal)",
                    ),
                ),
                AnatomySubgroup(
                    category = "Muscles",
                    items = listOf("Rectus Abdominis", "Transversus Abdominis", "Internal & External Obliques", "Erector Spinae"),
                ),
                AnatomySubgroup(
                    category = "Skeletal Anchor",
                    items = listOf("Lumbar Spine (L1–L5)", "Pelvic Rim / Iliac Crest"),
                ),
            ),
            functions = listOf(
                "Breaks down food enzymatically, absorbs nutrients, and filters metabolic waste.",
                "Stabilizes the lumbar spine and transfers kinetic power between upper and lower extremities.",
                "Regulates blood glucose, bile synthesis, and fluid/electrolyte balance.",
            ),
            interestingFacts = listOf(
                "The small intestine is roughly 6 meters (20 feet) long, with villi creating an immense absorptive surface area.",
                "The liver is the only internal human organ capable of regenerating lost tissue from as little as 25–30% of its mass.",
                "The enteric nervous system lining your gut holds over 100 million neurons—often called the 'second brain'.",
            ),
            relatedSystems = listOf("Digestive System", "Urinary System", "Muscular System", "Endocrine System"),
            accentColor = SkyBlue,
        ),
        BodyRegionId.LEGS to BodyRegionInfo(
            id = BodyRegionId.LEGS,
            name = "Legs",
            clinicalName = "Femoral, Patellar & Crural Regions",
            shortLabel = "LEGS",
            summary = "Weight-bearing propulsion pillars containing the longest bone and largest muscle groups in the body.",
            associatedAnatomy = listOf(
                AnatomySubgroup(
                    category = "Bones",
                    items = listOf("Femur (Thigh Bone)", "Patella (Kneecap)", "Tibia (Shinbone)", "Fibula"),
                ),
                AnatomySubgroup(
                    category = "Muscles",
                    items = listOf("Quadriceps Femoris", "Hamstrings", "Gluteus Maximus & Medius", "Gastrocnemius & Soleus"),
                ),
                AnatomySubgroup(
                    category = "Joints & Ligaments",
                    items = listOf("Hip Joint", "Knee Joint (ACL, PCL, MCL, LCL, Menisci)"),
                ),
            ),
            functions = listOf(
                "Supports full upright body weight against gravity during standing, walking, and running.",
                "Generates explosive propulsive force for jumping, sprinting, and stair climbing.",
                "Absorbs ground reaction shock through controlled eccentric muscle contractions.",
            ),
            interestingFacts = listOf(
                "The femur is both the longest and strongest bone in the human body—stronger than concrete under compression.",
                "The patella is a sesamoid bone embedded in the quadriceps tendon that boosts knee leverage by up to 30%.",
                "Your calf muscles act as a 'second heart,' squeezing deep veins to pump blood back toward the chest.",
            ),
            relatedSystems = listOf("Skeletal System", "Muscular System", "Cardiovascular System", "Nervous System"),
            accentColor = CobaltBlue,
        ),
        BodyRegionId.FEET to BodyRegionInfo(
            id = BodyRegionId.FEET,
            name = "Feet",
            clinicalName = "Tarsus, Metatarsus & Plantar Arch",
            shortLabel = "FEET",
            summary = "Spring-loaded anatomical foundations engineered to adapt to uneven terrain and propel every stride.",
            associatedAnatomy = listOf(
                AnatomySubgroup(
                    category = "Bones (26 per foot)",
                    items = listOf("7 Tarsals (Talus, Calcaneus/Heel)", "5 Metatarsals", "14 Phalanges (Toes)"),
                ),
                AnatomySubgroup(
                    category = "Connective & Muscular",
                    items = listOf("Achilles Tendon", "Plantar Fascia", "Tibialis Anterior & Posterior", "Intrinsic Foot Muscles"),
                ),
                AnatomySubgroup(
                    category = "Joints",
                    items = listOf("Talocrural (Ankle) Joint", "Subtalar Joint", "Plantar Arches"),
                ),
            ),
            functions = listOf(
                "Acts as a compliant shock absorber on heel strike and a rigid lever during toe-off.",
                "Maintains upright posture and balance through thousands of proprioceptive nerve endings.",
                "Stores and returns elastic energy in the plantar fascia and Achilles tendon during running.",
            ),
            interestingFacts = listOf(
                "Your two feet contain 52 bones—roughly one-quarter of all the bones in your entire body.",
                "The Achilles tendon is the thickest, strongest tendon in the body, handling forces over 10x body weight in sprints.",
                "Each foot contains around 200,000 nerve endings and over 125,000 sweat glands.",
            ),
            relatedSystems = listOf("Skeletal System", "Muscular System", "Nervous System (Proprioception)"),
            accentColor = ElectricCyan,
        ),
    )

    fun getRegion(id: BodyRegionId): BodyRegionInfo = regions.getValue(id)
}
