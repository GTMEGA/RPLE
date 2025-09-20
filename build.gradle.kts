plugins {
    id("com.falsepattern.fpgradle-mc") version ("1.1.3")
}

group = "com.falsepattern"

minecraft_fp {
    mod {
        modid = "rple"
        name = "Right Proper Lighting Engine"
        rootPkg = "$group.rple"
    }
    api {
        packages = listOf("api")
    }
    mixin {
        pkg = "internal.mixin.mixins"
        pluginClass = "internal.mixin.plugin.MixinPlugin"
    }
    core {
        accessTransformerFile = "rple_at.cfg"
        coreModClass = "internal.asm.ASMLoadingPlugin"
    }
    tokens {
        tokenClass = "internal.Tags"
        modid = "MOD_ID"
        name = "MOD_NAME"
        version = "VERSION"
        rootPkg = "GROUP_NAME"
    }
    publish {
        changelog = "https://github.com/GTMEGA/RPLE/releases/tag/$version"
        maven {
            repoUrl = "https://mvn.falsepattern.com/releases/"
            repoName = "mavenpattern"
        }
        curseforge {
            projectId = "1050511"
            dependencies {
                required("fplib")
                required("falsetweaks")
                required("lumi")
            }
        }
        modrinth {
            projectId = "glC7saXJ"
            dependencies {
                required("fplib")
                required("falsetweaks")
                required("lumi1710")
            }
        }
    }
}

repositories {
    cursemavenEX()
    exclusive(mega(), "codechicken", "mega")
    exclusive(mavenpattern(), "com.falsepattern", "makamys", "org.embeddedt.celeritas")
    exclusive(ivy("mavenpattern_mirror", "https://mvn.falsepattern.com/releases/mirror/", "[orgPath]/[artifact]-[revision].[ext]"), "mirror")
    exclusive(venmaven(), "com.ventooth")
}

dependencies {
    compileOnly("com.ventooth:swansong-mc1.7.10:1.0.0:dev")
    compileOnly("org.joml:joml:1.10.8")

    apiSplit("com.falsepattern:lumi-mc1.7.10:1.2.0")
    implementationSplit("com.falsepattern:falsepatternlib-mc1.7.10:1.9.0")
    implementationSplit("com.falsepattern:falsetweaks-mc1.7.10:4.0.0")

    val beddiumVersion = "1.0.0"
    val beddiumVersionJ21 = "$beddiumVersion-j21"
    val beddiumVersionJ8 = "$beddiumVersion-j8"
    compileOnly("com.ventooth:beddium-mc1.7.10:$beddiumVersionJ8:dev")
    modernJavaPatchDeps("com.ventooth:beddium-mc1.7.10:$beddiumVersionJ21:dev") {
        excludeDeps()
    }

    // Keep in sync with FalseTweaks!
    implementation("it.unimi.dsi:fastutil:8.5.16")

    compileOnly("makamys:neodymium-mc1.7.10:0.4.3-unofficial:dev")

    runtimeOnlyNonPublishable("codechicken:notenoughitems-mc1.7.10:2.4.1-mega:dev")
    runtimeOnlyNonPublishable("codechicken:codechickencore-mc1.7.10:1.4.0-mega:dev")

    // NEI 1.0.5.120
    compileOnly(deobfCurse("notenoughitems-222211:2302312"))
    // CCLib Unofficial 1.1.5.5
    compileOnly(deobfCurse("codechickenlib-746280:4192688"))
    // Chisel 2.9.5.11
    compileOnly(deobfCurse("chisel-235279:2287442"))
    // Carpenter's Blocks 3.3.8.2
    compileOnly(deobfCurse("carpentersblocks-228932:2719376"))
    // Storage Drawers 1.10.9
    compileOnly(deobfCurse("storagedrawers-223852:2469586"))
    // Project Red 4.7.0pre12.95 Core
    compileOnly(deobfCurse("projectredbase-228702:2280728"))
    // Project Red 4.7.0pre12.95 Illumination
    compileOnly(deobfCurse("projectredlighting-229046:2280733"))
    // MrTJPCore 1.1.0.33
    compileOnly(deobfCurse("mrtjpcore-229002:2279413"))
    // Forge Multipart 1.2.0.345
    compileOnly(deobfCurse("forgemultipart-229323:2242993"))
    // EnderCore 1.7.10-0.2.0.39_beta
    compileOnly(deobfCurse("endercore-231868:2331048"))
    // Ender IO 1.7.10-2.3.0.429_beta
    compileOnly(deobfCurse("ender-io-64578:2322348"))
    // ArchitectureCraft 1.7.2
    compileOnly(deobfCurse("architecturecraft-242001:2352554"))
    // Applied Energistics 2 rv3 beta 6
    compileOnly(deobfCurse("appliedenergistics2-223794:2296430"))
    // The Lord of the Rings Mod: Legacy v36.15
    compileOnly(deobfCurse("the-lord-of-the-rings-mod-legacy-423748:4091561"))
    // HBM NTM 1.0.27_X5027
    compileOnly(deobfCurse("hbm-ntm-235439:5534354"))
    // Fairy Lights 1.4.0
    compileOnly(deobfCurse("fairylights-233342:2270358"))
    // OpenModsLib-1.7.10-0.10-deobf.jar
    compileOnly("curse.maven:openmodslib-228815:2386729")
    // OpenBlocks-1.7.10-1.6-deobf.jar
    compileOnly("curse.maven:openblocks-228816:2386734")

    compileOnly("mirror:AM2.5:LTS-1.6.7-dev")
}
