package test;

/**
 * Breaks bndlib:2.3.0 with a method that contains a <code>invokeinterface ..., 19</code>
 * instruction followed by a <code>return</code> instruction (AMPS-1211).
 *
 * The third operand byte (13h) is misinterpreted by the affected version as the start of a new
 * <code>ldc_w</code> instruction, where the fourth operand byte (00h) and the next actual opcode
 * (B1h) form an <code>#index</code> (00B1h) beyond the constant pool of this small class.
 *
 * @see <a href="https://issues.apache.org/jira/browse/FELIX-4556">FELIX-4556</a>
 * @see <a href="https://github.com/bndtools/bnd/issues/603">bndtools/bnd#603</a>
 */
interface TestInterface {
    void method(
        int a, int b, int c, int d, int e, int f, int g, int h, int i, int j, int k, int l, int m,
        int n, int o, int p, int q, int r);

    default void test(TestInterface i) {
        i.method(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }
}
