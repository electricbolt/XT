// Interrupts.java
// XT Copyright © 2025; Electric Bolt Limited.

package nz.co.electricbolt.xt.usermode.interrupts;

import nz.co.electricbolt.xt.cpu.CPU;
import nz.co.electricbolt.xt.cpu.Reg16;
import nz.co.electricbolt.xt.cpu.Reg8;
import nz.co.electricbolt.xt.cpu.SegOfs;
import nz.co.electricbolt.xt.usermode.interrupts.annotations.*;
import nz.co.electricbolt.xt.usermode.util.DirectoryTranslation;
import nz.co.electricbolt.xt.usermode.util.MemoryUtil;
import nz.co.electricbolt.xt.usermode.util.Trace;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.TreeMap;

/**
 * Invokes a method that handles the interrupt ‣ function ‣ subfunction. Methods are defined using annotations and
 * invoked using reflection:
 * <pre>
 * &#64;Interrupt(function = 0x09, description = "Write string to standard output")
 * public void writeString(CPU cpu, @ASCIZ(terminationChar = '$') @DS @DX String s) {
 *    System.out.print(s);
 *    cpu.getReg().AL.setValue((byte) '$');
 * }
 * </pre>
 *
 * Any method declared with an &#64;Interrupt annotation will be inspected. The interrupt annotation contains the
 * following parameters:
 * <ul>
 *     <li>interrupt - if not specified, defaults to the value 0x21.</li>
 *     <li>function - the function number that would be in the AH register.</li>
 *     <li>subfunction - an optional subfunction number that would be in the AL register.</li>
 *     <li>description - description for a developer.</li>
 * </ul>
 *
 * The method declaration must have parameters declared in the following order:
 * <ul>
 *     <li>CPU - mandatory, as every interrupt handler method requires access to the CPU, registers and memory.</li>
 *     <li>Trace - optional, used to output tracing info to the trace file (if enabled).</li>
 *     <li>DirectoryTranslation - optional, used to convert emulated directory to host directories and vice versa.</li>
 *     <li>Register parameters - optional, if the interrupt function uses registers as request parameters.</li>
 * </ul>
 *
 * Register parameters must be one of the following types:
 * <ul>
 *     <li>byte - Prefixed by AL, BL, CL, DL, AH, BH, CH or DH annotation.</li>
 *     <li>boolean - Prefixed by AL, BL, CL, DL, AH, BH, CH or DH annotation. 0 is true, != 0 is false.</li>
 *     <li>char - Prefixed by AL, BL, CL, DL, AH, BH, CH or DH annotation.</li>
 *     <li>short - Prefixed by AX, BX, CX, DX, SI or DI annotation.</li>
 *     <li>int - Prefixed by two annotations of AX, BX, CX, DX, SI or DI. The first annotation will be the high-order
 *     word, and the second annotation will be the low-order word.</li>
 *     <li>SegOfs - Prefixed by DS or ES; and AX, BX, CX, DX, SI or DI.</li>
 *     <li>String - Prefixed by ASCIZ, then DS or ES; and AX, BX, CX, DX, SI or DI.</li>
 * </ul>
 *
 * Methods can always access registers directly if the above list of property types is not enough.
 */

public class Interrupts {

    private final Map<String, InterruptImpl> implMap;

    public Interrupts() {
        this.implMap = new TreeMap<>();
        loadClass("bios.TimeDate");
        loadClass("dos.ConsoleIO");
        loadClass("dos.FileDateTime");
        loadClass("dos.FileIO");
        loadClass("dos.InterruptVectors");
        loadClass("dos.Misc");
        loadClass("dos.Memory");
        loadClass("dos.TimeDate");
        loadClass("dos.TerminateProgram");
    }

    /**
     * Invokes a method that handles the interrupt ‣ function ‣ subfunction. Currently only supports AH (function) and
     * optional an AL (subfunction).
     */
    public void execute(final CPU cpu, final byte interrupt, final Trace trace, final DirectoryTranslation directoryTranslation) {
        // Check for interrupt + AH match.
        String key = String.format("%02X %02X", interrupt, cpu.getReg().AH.getValue());
        InterruptImpl impl = implMap.get(key);
        if (impl == null) {
            // Check for interrupt + AH + AL match.
            key = String.format("%02X %02X %02X", interrupt, cpu.getReg().AH.getValue(), cpu.getReg().AL.getValue());
            impl = implMap.get(key);
            if (impl == null) {
                trace.log(String.format("Unhandled interrupt %02X%n", interrupt));
                trace.log(cpu.getReg().toString());
                System.err.printf("Unhandled interrupt %02X%n", interrupt);
                System.err.println(cpu.getReg().toString());
                System.exit(255);
            }
        }

        final StringBuilder debugBuf = new StringBuilder();
        debugBuf.append(impl.description);
        debugBuf.append(" ");

        final Parameter[] parameters = impl.clazzMethod.getParameters();
        final Object[] args = new Object[parameters.length];

        // Mandatory CPU and optional Trace and DirectoryTranslation parameters.
        int argIndex = 0;
        args[argIndex++] = cpu;
        if (argIndex < parameters.length && parameters[argIndex].getType() == Trace.class)
            args[argIndex++] = trace;
        if (argIndex < parameters.length && parameters[argIndex].getType() == DirectoryTranslation.class)
            args[argIndex++] = directoryTranslation;

        // Optional register parameters.
        for (int p = argIndex; p < parameters.length; p++) {
            final Parameter parameter = parameters[p];
            final Annotation[] annotations = parameter.getDeclaredAnnotations();

            if (parameter.getType() == byte.class) {
                final Reg8 reg8 = annotationToReg8(cpu, annotations[0]);
                final byte value = reg8.getValue();
                debugBuf.append(String.format("%s=%02X", reg8.getName(), value));
                args[p] = value;
            } else if (parameter.getType() == boolean.class) {
                final Reg8 reg8 = annotationToReg8(cpu, annotations[0]);
                final boolean value = (reg8.getValue() != 0);
                debugBuf.append(reg8.getName());
                debugBuf.append(value ? "=true" : "=false");
                args[p] = value;
            } else if (parameter.getType() == char.class) {
                final Reg8 reg8 = annotationToReg8(cpu, annotations[0]);
                final char value = (char) reg8.getValue();
                debugBuf.append(reg8.getName());
                debugBuf.append(value);
                args[p] = value;
            } else if (parameter.getType() == short.class) {
                final Reg16 reg16 = annotationToReg16(cpu, annotations[0]);
                final short value = reg16.getValue();
                debugBuf.append(String.format("%s=%04X", reg16.getName(), value));
                args[p] = value;
            } else if (parameter.getType() == int.class) {
                final Reg16 hi = annotationToReg16(cpu, annotations[0]);
                final Reg16 lo = annotationToReg16(cpu, annotations[1]);
                final int value = (hi.getValue() << 16 & 0xFFFF) | (lo.getValue() & 0xFFFF);
                debugBuf.append(String.format("%s,%s=%08X", hi.getName(), lo.getName(), value));
                args[p] = value;
            } else if (parameter.getType() == SegOfs.class) {
                final Reg16 seg = annotationToReg16(cpu, annotations[0]);
                final Reg16 ofs = annotationToReg16(cpu, annotations[1]);
                final SegOfs value = new SegOfs(seg, ofs);
                debugBuf.append(String.format("%s:%s=%s", seg.getName(), ofs.getName(), value));
                args[p] = value;
            } else if (parameter.getType() == String.class) {
                // Strings are converted to CP437 to display IBM PC MDA/CGA/EGA/VGA characters (e.g. line drawing).
                final char terminationChar = ((ASCIZ) annotations[0]).terminationChar();
                final Reg16 seg = annotationToReg16(cpu, annotations[1]);
                final Reg16 ofs = annotationToReg16(cpu, annotations[2]);
                final String value = MemoryUtil.readStringZ(cpu.getMemory(), new SegOfs(seg, ofs), terminationChar);
                final StringBuilder debugValue = new StringBuilder();
                for (int i = 0; i < value.length(); i++) {
                    byte b = (byte) value.charAt(i);
                    if (b >= 32)
                        debugValue.append(new String(new byte[] { b }, Charset.forName("Cp437")));
                    else
                        debugValue.append('☐');
                }
                debugBuf.append(String.format("%s:%s=\"%s\"", seg.getName(), ofs.getName(), debugValue));
                args[p] = value;
            }
            debugBuf.append(" ");
        }

        trace.interrupt(debugBuf.toString());

        try {
            impl.clazzMethod.invoke(impl.instance, args);
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            trace.log(String.format("Exception thrown executing interrupt %02X%n - %s", interrupt, e));
            trace.log(cpu.getReg().toString());
            System.err.printf("Exception thrown executing interrupt %02X%n - %s", interrupt, e);
            System.err.println(cpu.getReg().toString());
            System.exit(255);
        }
    }

    /**
     * Invoked by <code>Main.parseInt()</code> to print out the current list of implemented interrupt handling functions.
     */
    public void printInterrupts() {
        for (final InterruptImpl impl : implMap.values()) {
            System.out.println(impl.description);
        }
    }

    /**
     * Loads the class specified by className that implements one or more interrupt handling functions.
     * All interrupt handling function parameters and annotations are checked for correctness. XT will terminate
     * if the developer has made an error in implementation.
     * @param className The name of the class to load, prefixed by the immediate package. e.g. "dos.ConsoleIO".
     */
    private void loadClass(final String className) {
        try {
            final String packagePrefix = this.getClass().getPackageName();
            final Class<?> clazz = Class.forName(packagePrefix + "." + className);
            final Method[] clazzMethods = clazz.getDeclaredMethods();
            Object instance = null;

            for (final Method clazzMethod : clazzMethods) {
                final Annotation[] annotations = clazzMethod.getDeclaredAnnotations();
                for (final Annotation annotation : annotations) {
                    if (annotation.annotationType() == Interrupt.class) {
                        checkClassMethod(clazzMethod);

                        final Interrupt api = (Interrupt) annotation;
                        final StringBuilder keyBuf = new StringBuilder();
                        final StringBuilder descBuf = new StringBuilder();

                        keyBuf.append(String.format("%02X ", api.interrupt()));
                        descBuf.append(String.format("INT %02X function ", api.interrupt()));

                        keyBuf.append(String.format("%02X", api.function()));
                        descBuf.append(String.format("%02X", api.function()));

                        if (api.subfunction() != -1) {
                            keyBuf.append(String.format(" %02X", api.subfunction()));
                            descBuf.append(String.format("%02X", api.subfunction()));
                        }

                        descBuf.append(" - ");
                        descBuf.append(api.description());

                        if (instance == null)
                            instance = clazz.getDeclaredConstructor().newInstance();

                        final InterruptImpl method = new InterruptImpl(keyBuf.toString(), api.interrupt(),
                                api.function(), api.subfunction(), descBuf.toString(), instance, clazzMethod);
                        implMap.put(keyBuf.toString(), method);
                    }
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks the method to ensure all the parameters are correctly defined, in the right order and have appropriate
     * annotations. XT will terminate if the developer has made an error in implementation.
     */

    private void checkClassMethod(final Method clazzMethod) {
        final Parameter[] parameters = clazzMethod.getParameters();

        if (parameters.length == 0 || parameters[0].getType() != CPU.class)
            haltError("method " + clazzMethod.getName() + " first parameter declared must be CPU.");

        int traceIndex = -1;
        int directoryTranslationIndex = -1;
        int registerIndex = -1;
        for (int i = 1; i < parameters.length; i++) {
            final Class<?> parameterType = parameters[i].getType();

            if (parameterType == Trace.class) {
                if (traceIndex != -1)
                    haltError("method " + clazzMethod.getName() + " can only have one Trace parameter.");
                if (directoryTranslationIndex != -1)
                    haltError("method " + clazzMethod.getName() + " Trace parameter must be declared before DirectoryTranslation parameter.");
                if (registerIndex != -1)
                    haltError("method " + clazzMethod.getName() + " must be declared before any annotated parameter.");
                traceIndex = i;
            } else if (parameterType == DirectoryTranslation.class) {
                if (directoryTranslationIndex != -1)
                    haltError("method " + clazzMethod.getName() + " can only have one DirectoryTranslation parameter.");
                if (i < traceIndex)
                    haltError("method " + clazzMethod.getName() + " DirectoryTranslation parameter must be declared after Trace parameter.");
                if (registerIndex != -1)
                    haltError("method " + clazzMethod.getName() + " DirectoryTranslation must be declared before any annotated parameter.");
                directoryTranslationIndex = i;
            } else {
                final Annotation[] annotations = parameters[i].getDeclaredAnnotations();
                registerIndex = i;
                if (parameterType == byte.class) {
                    if (annotations.length != 1 || checkReg8(annotations[0].annotationType()))
                        haltError("method " + clazzMethod.getName() + " byte parameter must be annotated with AL, BL, CL, DL, AH, BH, CH or DH.");
                } else if (parameterType == boolean.class) {
                    if (annotations.length != 1 || checkReg8(annotations[0].annotationType()))
                        haltError("method " + clazzMethod.getName() + " boolean parameter must be annotated with AL, BL, CL, DL, AH, BH, CH or DH.");
                } else if (parameterType == char.class) {
                    if (annotations.length != 1 || checkReg8(annotations[0].annotationType()))
                        haltError("method " + clazzMethod.getName() + " char parameter must be annotated with AL, BL, CL, DL, AH, BH, CH or DH.");
                } else if (parameterType == short.class) {
                    if (annotations.length != 1 || checkReg16(annotations[0].annotationType()))
                        haltError("method " + clazzMethod.getName() + " short parameter must be annotated with AX, BX, CX, DX, SI or DI.");
                } else if (parameterType == int.class) {
                    if (annotations.length != 2 || checkReg16(annotations[0].annotationType()) || checkReg16(annotations[1].annotationType()))
                        haltError("method " + clazzMethod.getName() + " int parameter must be annotated with AX, BX, CX, DX, SI or DI.");
                    if (annotations[0].annotationType() == annotations[1].annotationType())
                        haltError("method " + clazzMethod.getName() + " int parameter must be annotated with two different registers.");
                } else if (parameterType == SegOfs.class) {
                    if (annotations.length != 2 || checkSegReg16(annotations[0].annotationType()) || checkReg16(annotations[1].annotationType()))
                        haltError("method " + clazzMethod.getName() + " SegOfs parameter must be annotated with DS or ES; and AX, BX, CX, DX, SI or DI.");
                } else if (parameterType == String.class) {
                    if (annotations.length != 3 || (annotations[0].annotationType() != ASCIZ.class) || checkSegReg16(annotations[1].annotationType()) || checkReg16(annotations[2].annotationType()))
                        haltError("method " + clazzMethod.getName() + " SegOfs parameter must be annotated with ASCIZ; DS or ES; and AX, BX, CX, DX, SI or DI.");
                } else
                    haltError("method " + clazzMethod.getName() + " parameter must be of type byte, boolean, char, short, int, SegOfs or String.");
            }
        }
    }

    private void haltError(final String message) {
        System.err.println("Developer error: " + message);
        System.exit(255);
    }

    private boolean checkReg8(final Class<? extends Annotation> annotationType) {
        return annotationType != AL.class && annotationType != AH.class && annotationType != BL.class &&
                annotationType != BH.class && annotationType != CL.class && annotationType != CH.class &&
                annotationType != DL.class && annotationType != DH.class;
    }

    private boolean checkReg16(final Class<? extends Annotation> annotationType) {
        return annotationType != AX.class && annotationType != BX.class &&
                annotationType != CX.class && annotationType != DX.class &&
                annotationType != SI.class && annotationType != DI.class;
    }

    private boolean checkSegReg16(final Class<? extends Annotation> annotationType) {
        return annotationType != DS.class && annotationType != ES.class;
    }

    private Reg8 annotationToReg8(final CPU cpu, final Annotation annotation) {
        if (annotation.annotationType() == AL.class)
            return cpu.getReg().AL;
        else if (annotation.annotationType() == AH.class)
            return cpu.getReg().AH;
        else if (annotation.annotationType() == BL.class)
            return cpu.getReg().BL;
        else if (annotation.annotationType() == BH.class)
            return cpu.getReg().BH;
        else if (annotation.annotationType() == CL.class)
            return cpu.getReg().CL;
        else if (annotation.annotationType() == CH.class)
            return cpu.getReg().CH;
        else if (annotation.annotationType() == DL.class)
            return cpu.getReg().DL;
        else if (annotation.annotationType() == DH.class)
            return cpu.getReg().DH;
        else
            throw new IllegalArgumentException("Unknown annotation " + annotation.annotationType() + " for Reg8");
    }

    private Reg16 annotationToReg16(final CPU cpu, final Annotation annotation) {
        if (annotation.annotationType() == AX.class)
            return cpu.getReg().AX;
        else if (annotation.annotationType() == BX.class)
            return cpu.getReg().BX;
        else if (annotation.annotationType() == CX.class)
            return cpu.getReg().CX;
        else if (annotation.annotationType() == DX.class)
            return cpu.getReg().DX;
        else if (annotation.annotationType() == DS.class)
            return cpu.getReg().DS;
        else if (annotation.annotationType() == SI.class)
            return cpu.getReg().SI;
        else if (annotation.annotationType() == ES.class)
            return cpu.getReg().ES;
        else if (annotation.annotationType() == DI.class)
            return cpu.getReg().DI;
        else
            throw new IllegalArgumentException("Unknown annotation " + annotation.annotationType() + " for Reg16");
    }

    private record InterruptImpl(String key, int interrupt, int function, int subfunction, String description,
                                 Object instance, Method clazzMethod) {
    }
}
