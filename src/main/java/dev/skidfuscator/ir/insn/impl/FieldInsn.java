package dev.skidfuscator.ir.insn.impl;

import dev.skidfuscator.ir.FunctionNode;
import dev.skidfuscator.ir.field.FieldNode;
import dev.skidfuscator.ir.hierarchy.Hierarchy;
import dev.skidfuscator.ir.insn.AbstractInsn;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

public class FieldInsn extends AbstractInsn {
    private final FieldInsnNode node;
    private FieldNode target;

    public FieldInsn(Hierarchy hierarchy, FieldInsnNode node) {
        super(hierarchy);
        this.node = node;
    }

    @Override
    public void resolve() {
        this.target = hierarchy.findField(node);
    }

    @Override
    public void dump() {
        this.node.owner = target.getParent().getName();
        this.node.name = target.getName();
        this.node.desc = target.getDesc();
    }

    public boolean isStatic() {
        return target.isStatic();
    }
}
