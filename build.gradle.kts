plugins {
    id("fpgradle-minecraft") version ("0.8.2")
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
    mavenpattern {
        content {
            includeGroup("com.falsepattern")
        }
    }
    mega {
        content {
            includeGroups("mega", "codechicken", "team.chisel")
        }
    }
    maven("mega2", "https://mvn.falsepattern.com/gtmega_uploads/") {
        content {
            includeGroup("optifine")
        }
    }
}

dependencies {
    apiSplit("com.falsepattern:lumi-mc1.7.10:1.0.1")
    implementationSplit("com.falsepattern:falsepatternlib-mc1.7.10:1.4.4")
    implementationSplit("com.falsepattern:falsetweaks-mc1.7.10:3.3.2")

    devOnlyNonPublishable("codechicken:notenoughitems-mc1.7.10:2.3.1-mega:dev")
    runtimeOnlyNonPublishable("codechicken:codechickencore-mc1.7.10:1.4.0-mega:dev")

    compileOnly("optifine:optifine:1.7.10_hd_u_e7:dev")

    compileOnly("team.chisel:chisel-mc1.7.10:2.14.7-mega:dev")
    compileOnly("mega:carpentersblocks-mc1.7.10:3.4.1-mega:dev")

    compileOnly("mega:storagedrawers-mc1.7.10:1.14.1-mega:dev")

    compileOnly("mega:projectred-mc1.7.10:5.0.0-mega:dev")
    compileOnly("mega:mrtjpcore-mc1.7.10:1.2.1-mega:dev")
    compileOnly("codechicken:forgemultipart-mc1.7.10:1.6.2-mega:dev")

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
}
