# XT 

**A ~~unit test framework for 8086/8088 assembly *and*~~ MS-DOS command line program user mode emulator.**

### What is XT?

1. ~~**Unit test framework** for 8086/8088 assembly code. You write your test specifications as a plain text file. Tests are executed against the public symbols of your compiled `.OBJ` files.~~ _Coming soon._

2. **Run MS-DOS command line programs** - a user mode emulator similar to WINE or QEMU but for MS-DOS command line 
programs. Loads and executes `.COM` and `.EXE` files. MS-DOS interrupts are translated to your native OS APIs. See 
[Running MS-DOS command line programs](#running-ms-dos-command-line-programs) for a compatible list of programs. 
**Example:**

```bash
[user@host ~]# xt run TASM.EXE

Turbo Assembler  Version 3.2  Copyright (c) 1988, 1992 Borland International
Syntax:  TASM [options] source [,object] [,listing] [,xref]
/a,/s         Alphabetic or Source-code segment ordering
/c            Generate cross-reference in listing
/dSYM[=VAL]   Define symbol SYM = 0, or = value VAL
/e,/r         Emulated or Real floating-point instructions
/h,/?         Display this help screen
/iPATH        Search PATH for include files
/jCMD         Jam in an assembler directive CMD (eg. /jIDEAL)
/kh#          Hash table capacity # symbols
...

[user@host ~]# xt run TASM.EXE MORE.ASM

Turbo Assembler  Version 3.2  Copyright (c) 1988, 1992 Borland International

Assembling file:   MORE.ASM
Error messages:    None
Warning messages:  None
Passes:            1
Remaining memory:  795k

[user@host ~]#
```

It is **not** a goal of XT to evolve into runnning full screen text or graphical programs. There are other solutions for
this such as [QEMU](https://www.qemu.org/), [dosbox-x](https://dosbox-x.com/) or 
[MartyPC](https://github.com/dbalsom/martypc).

#### 8086/8088 CPU emulation compatibility

The underlying CPU emulator implements all *documented* 8086/8088 instructions. 

The undocumented instructions are as follows, which are *not* implemented (as you *generally* don't write them in
assembly, and your code would be incompatible with a V20 CPU):

- **0F** - `POP CS`.
- **60-6F** - Conditional jumps (identical to **70-7F**)
- **82** - 8-bit immediate group (identical to **80**).
- **C0** - `RET` - Near return (identical to **C2**).
- **C1** - `RET` - Near return (identical to **C3**).
- **C8** - `RET` - Far return (identical to **CA**).
- **C9** - `RET` - Far return (identical to **C9**).
- **D0/6** - Set minus one.
- **D1/6** - Set minus one.
- **D2/6** - Unknown.
- **D3/6** - Unknown.
- **D6** - `SALC`.
- **F1** - `LOCK` (identical to **F0**).
- **F6/1** - `TEST` (identical to **F6/0**).
- **FE/2-7** - `CALL`,`JMP`,`PUSH`.
- **FF/7** - `PUSH` (identical to **F7/6**). 

The CPU emulator is unit tested against [Single Step Tests](https://github.com/electricbolt/8088) 
(over 2.5 million tests) and all documented opcodes pass including exact *undocumented* flag behaviour (except for 
instructions **F6/6** `DIV` (8-bit), **F7/6** `DIV` (16-bit), **F6/7** `IDIV` (8-bit), **F7/7** `IDIV` (16-bit), which 
would require a full microcode implementation of the algorithms, these instructions clear the undocumented flags 
instead).

### Installing XT

XT should run on any Java 17 or greater PC, including Windows, Mac or Linux. Intel or ARM.
Use the GitHub [Releases](https://github.com/electricbolt/xttest/releases) link to download. XT is only ~110KB installed.

#### MacOS

1. Create a `XT` directory. e.g. `~/XT`.
2. Unzip the contents of the downloaded zip file into the `XT` directory.
3. Edit the `~/.zshrc` file to add `$HOME/XT` to the `PATH`. e.g. `PATH="$HOME/XT:$PATH"`.

#### Windows

1. Create a `XT` directory, e.g. `C:\Users\<username>\Documents\XT`.
2. Unzip the contents of the downloaded zip file into the `XT` directory.
3. Edit `Environment Variables`, modify `PATH` to include your directory. e.g. `C:\Users\<username>\Documents\XT`.

#### Linux (Ubuntu)

1. Create a `XT` directory. e.g. `~/XT`.
2. Unzip the contents of the downloaded zip file into the `XT` directory.
3. Edit the `~/.bashrc` file to add `~/XT` to the `PATH`. e.g. `PATH="~/XT:$PATH"`.

### Running MS-DOS command line programs

XT can directly run, on your Windows, Mac or Linux PC, in a command shell/terminal, the following MS-DOS
command line programs:

| Program         | Version(s) | File      | Description                                          |                                 
|-----------------|-----------|-----------|------------------------------------------------------|
| Turbo Assembler | 2.01      | TASM.EXE  |                                                      |
| Turbo Assembler | 3.2       | TASM.EXE  |                                                      |
| Turbo Linker    | 3.0.1     | TLINK.EXE | 3.0.1 is the last version compatible with 8086/8088. |
| Turbo Librarian | 3.0       | TLIB.EXE  |                                                      |
| Turbo Pascal    | 5.5       | TPC.EXE   |                                                      |
| Turbo Pascal    | 6.0       | TPC.EXE   | 6.0 is the last version compatible with 8086/8088.   |
| INFOEXE         | 1.0       | INFOEXE   | Information about an EXE file by Fabrice Bellard.    |

`xt run [-c dir] PROGRAM.EXE [command line arguments]`

The optional `-c dir` is the host directory that will be the root of the emulated `C:` drive. If not specified, then the 
current working directory will be used.

All directories and filenames provided to a MS-DOS command line program should conform to 8.3 character limitation and 
be uppercase.

Because there is no Video RAM, ROM BIOS, Option ROMs or DOS kernel in memory, programs get almost the entire 1MB address
space to run. (Minus 2.5KB for interrupt vector table, BIOS data area, program segment prefix and environment 
variables).

### Extending/Developing XT ###

XT is developed with the following:
* Java 17 JDK or greater.
* Maven.
* IntelliJ IDEA (or your favourite editor).

To build XT and run its unit tests, you will need to clone 
[Single Step Tests](https://github.com/electricbolt/8088) into `src/test/resources/` 
(the `8088` directory should be at the same level as `junit-platform.properties`).

The `build.sh` script has been tested on a Mac. It cleans, compiles, unit tests and packages a release zip. All unit
tests _including_ the 2.5 million Single Step Tests take about 40 seconds on an Apple MacBook Pro M4.

#### Adding DOS or BIOS interrupt handling functions to allow additional MS-DOS programs to run

1. Try running your MS-DOS command line program. Any unhandled DOS or BIOS interrupts cause XT to terminate and
display an error message. For the following example, you would need to implement an interrupt handling function for 
Interrupt 0x10, Function 0x0F - Get current video mode:

```text
Unhandled interrupt 10
CS=0000 IP=0000 FLAGS=ZF PF AX=0F00 BX=02DE CX=0CFE DX=D7B2 DS=03E0 SI=0207 ES=03E0 DI=03EE SS=0434 SP=0BE8 BP=0BFA
```

2. Add your DOS or BIOS interrupt handling function to an existing java file (assuming the same category of 
functionality) or create a new file. If creating a new file you must also amend the `Interrupts.java` constructor to 
ensure your class is loaded using `loadClass` at startup.
3. Ensure your DOS or BIOS interrupt handling function has the appropriate annotations: `@Interrupt`, `@AX`, `@BX`, 
`@ASCIZ` etc. See `Interrupts.java` class comment for more details.
4. For debugging, enable Interrupt and CPU tracing by adding `-ti -tc trace.txt` command line parameters. You can also
modify `READ/WRITE/EXECUTE` permissions for each byte of memory to isolate program behavior. Uncomment/customize the 
memory protection code in `ProgramLoader.java`.
5. Add any appropriate unit tests.
6. Submit a PR, and we'll do our best to review; time and workload permitting.

#### Implemented DOS or BIOS interrupt handling functions

- **INT 1A function 00** - Get system time clock ticks
- **INT 20 function 00** - Terminate program
- **INT 21 function 00** - Terminate program
- **INT 21 function 02** - Write character to standard output
- **INT 21 function 09** - Write string to standard output
- **INT 21 function 1A** - Set disk transfer area address
- **INT 21 function 25** - Set interrupt vector
- **INT 21 function 2A** - Get system date
- **INT 21 function 2C** - Get system time
- **INT 21 function 2F** - Get disk transfer area address
- **INT 21 function 30** - Get DOS version
- **INT 21 function 3301** - Set extended break checking state
- **INT 21 function 35** - Get interrupt vector
- **INT 21 function 3700** - Get switch character
- **INT 21 function 38** - Get country specific information
- **INT 21 function 3C** - Create or truncate file
- **INT 21 function 3D** - Open existing file
- **INT 21 function 3E** - Close file
- **INT 21 function 3F** - Read from file or device
- **INT 21 function 40** - Write to file or device
- **INT 21 function 41** - Delete file
- **INT 21 function 42** - Set current file position
- **INT 21 function 4300** - Get file attributes
- **INT 21 function 4400** - IOCTL Get device information
- **INT 21 function 4401** - IOCTL Set device information
- **INT 21 function 4407** - IOCTL Get input/output status
- **INT 21 function 4A** - Resize memory block
- **INT 21 function 4C** - Terminate program with exit code
- **INT 21 function 4E** - Find first matching file
- **INT 21 function 56** - Rename file
- **INT 21 function 5700** - Get file's date and time
