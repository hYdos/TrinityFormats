package me.hydos.pokefiletools.sv.json.trskl;

import me.hydos.pokefiletools.sv.json.shared.MayaTransformMatrix;
import org.joml.Vector3f;

import java.util.ArrayList;

public class TrinitySkeleton {

    public int res_0;
    public ArrayList<TransformNode> transform_nodes;
    public ArrayList<Bone> bones;
    public ArrayList<InverseKinematics> iks;
    public int rig_offset;

    public TrinitySkeleton() {
        bones = new ArrayList<>();
        transform_nodes = new ArrayList<>();
        iks = new ArrayList<>();
    }

    public static class TransformNode {
        public String name;
        public Transform transform;
        public Vector3f scalePivot;
        public Vector3f rotatePivot;
        public int parent_idx;
        public int rig_idx;
        public String effect_node;
        public String type;

        public static class Transform {
            public Vector3f VecScale;
            public Vector3f VecRot;
            public Vector3f VecTranslate;

            public Transform() {
            }

            public Transform(Vector3f vecScale, Vector3f vecRot, Vector3f vecTranslate) {
                VecScale = vecScale;
                VecRot = vecRot;
                VecTranslate = vecTranslate;
            }
        }

        public TransformNode() {
        }

        public TransformNode(String name, Transform transform, Vector3f scalePivot, Vector3f rotatePivot, int parent_idx, int rig_idx, String effect_node, String type) {
            this.name = name;
            this.transform = transform;
            this.scalePivot = scalePivot;
            this.rotatePivot = rotatePivot;
            this.parent_idx = parent_idx;
            this.rig_idx = rig_idx;
            this.effect_node = effect_node;
            this.type = type;
        }
    }

    public static class Bone {
        public int inherit_position;
        public int unk_bool_2;
        public MayaTransformMatrix matrix;

        public Bone() {
        }

        public Bone(int inherit_position, int unk_bool_2, MayaTransformMatrix matrix) {
            this.inherit_position = inherit_position;
            this.unk_bool_2 = unk_bool_2;
            this.matrix = matrix;
        }
    }

    public static class InverseKinematics {
    }
}
