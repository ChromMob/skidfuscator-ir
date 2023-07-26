package dev.skidfuscator.ir.insn.impl;

import dev.skidfuscator.ir.field.FieldInvoker;
import dev.skidfuscator.ir.field.FieldNode;
import dev.skidfuscator.ir.field.invoke.StaticFieldInvoke;
import dev.skidfuscator.ir.hierarchy.Hierarchy;
import dev.skidfuscator.ir.insn.AbstractInsn;
import dev.skidfuscator.ir.insn.InstructionList;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;

public class FieldInsn extends AbstractInsn {
    private final FieldInsnNode node;
    private FieldInvoker<FieldInsn> invoke;
    private boolean synthetic;

    public FieldInsn(Hierarchy hierarchy, FieldInsnNode node) {
        super(hierarchy, node);
        this.node = node;
    }

    @Override
    public void resolve() {
        this.invoke = new StaticFieldInvoke(this);

        final FieldNode target = hierarchy.findField(node);

        if (target == null) {
            throw new IllegalStateException(String.format(
                    "Could not find target for %s.%s%s",
                    node.owner, node.name, node.desc
            ));
        }

        this.invoke.setTarget(target);
        super.resolve();
    }

    @Override
    public AbstractInsnNode dump() {
        this.node.owner = invoke.getTarget().getParent().getName();
        this.node.name = invoke.getTarget().getName();
        this.node.desc = invoke.getTarget().getType().getInternalName();

        return this.node;
    }

    public String getName() {
        return invoke.getTarget().getName();
    }

    public String getType() {
        return invoke.getTarget().getType().getInternalName();
    }

    @Override
    public void setParent(InstructionList parent) {
        // Unique scenario: when instruction does not have
        // a parent, remove it from the invocation list
        // only re-add when the parent is set
        if (parent == null) {
            this.invoke.getTarget().removeInvoke(this.invoke);
            this.synthetic = true;
        } else if (this.synthetic) {
            System.out.println("Adding invoke " + this.invoke.getTarget() + " to " + parent);
            this.invoke.getTarget().addInvoke(this.invoke);
        }

        super.setParent(parent);
    }

    public boolean isStatic() {
        return invoke.getTarget().isStatic();
    }

    @Override
    public String toString() {
        final boolean fetch = this.node.getOpcode() == Opcodes.GETSTATIC
                || this.node.getOpcode() == Opcodes.GETFIELD;

        return fetch
                ? "push(field) " + invoke.getTarget()
                : invoke.getTarget() + " = pop()";
    }
}