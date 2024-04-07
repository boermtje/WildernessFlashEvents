package net.botwithus;

import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;
import net.botwithus.rs3.script.ScriptConsole;
import net.botwithus.rs3.script.ScriptGraphicsContext;

public class SkeletonScriptGraphicsContext extends ScriptGraphicsContext {

    private SkeletonScript script;

    public SkeletonScriptGraphicsContext(ScriptConsole scriptConsole, SkeletonScript script) {
        super(scriptConsole);
        this.script = script;
    }

    @Override
    public void drawSettings() {
        if (ImGui.Begin("Wilderness Flash Events", ImGuiWindowFlag.None.getValue())) {
            if (ImGui.BeginTabBar("My bar", ImGuiWindowFlag.None.getValue())) {
                if (ImGui.BeginTabItem("Settings", ImGuiWindowFlag.None.getValue())) {
                    ImGui.Text("Infernal star & Evil Bloodwood Tree only");
                    ImGui.Text("Read Requirements before starting");
                    ImGui.Text("Wilderness Flash Event is doing: " + script.getBotState());
                    if (ImGui.Button("Start")) {
                        //button has been clicked
                        script.setBotState(SkeletonScript.BotState.INBETWEEN_EVENTS);
                    }
                    ImGui.SameLine();
                    if (ImGui.Button("Stop")) {
                        //has been clicked
                        script.setBotState(SkeletonScript.BotState.IDLE);
                    }
                    ImGui.EndTabItem();
                }
                    if (ImGui.BeginTabItem("Requirements", ImGuiWindowFlag.None.getValue())) {
                        ImGui.Text("1: Walk to the event yourself");
                        ImGui.Text("2: Have chat open so script can recognize stun & Event completion");
                        ImGui.Text("For Pyrefiends specific:");
                        ImGui.Text("1: Equip a combat preset");
                        ImGui.Text("2: Have Eat Food ability in abilitybar for automatic eating");
                        ImGui.Text("That should be all, sit back and relax");
                    }
                    ImGui.EndTabItem();
                ImGui.EndTabBar();
            }
            ImGui.End();
        }

    }

    @Override
    public void drawOverlay() {
        super.drawOverlay();
    }
}
