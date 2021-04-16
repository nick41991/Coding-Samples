.section .data
.section .bss
.section .text
.globl my_func
my_func:
pushq %rbp
movq %rsp, %rbp
movl -0(%ebp), %eax
movl -4(%ebp), %ecx
movl %eax, %ecx
movl -8(%ebp), %edx
movl %edx, %eax
popq %rbp
ret

.globl main
main:
pushq %rbp
movq %rsp, %rbp
movl -12(%ebp), %ebx
movl $1, %ebx
movl -16(%ebp), %esi
movl -20(%ebp), %edi
movl %esi, %edi
LABEL:
subl $10, %esi
and $0x80000000, %esi
movl -24(%ebp), %r8d
movl %esi, %r8d
movl -28(%ebp), %r9d
movl %r8d, %r9d
movl -32(%ebp), %r10d
movl %r9d, %r10d
movl -36(%ebp), %r12d
movl -40(%ebp), %r13d
notl %r12d
movl %r12d, %r13d
TOP_WHILE0:
movl -44(%ebp), %r14d
movl $1, %r14d
addl %esi, %r14d
movl -48(%ebp), %r15d
movl %r14d, %r15d
movl %eax, -0(%ebp)
movl -52(%ebp), %eax
movl %esi, %eax
movl %ecx, -4(%ebp)
movl -56(%ebp), %ecx
movl %edx, -8(%ebp)
movl -60(%ebp), %edx
movl %ecx, %edx
movl %ebx, -12(%ebp)
movl -64(%ebp), %ebx
movl %edx, %ebx
cmpl %ebx, 0
jne IF0
jmp WHILE0
IF0:
subl $10, %esi
and $0x80000000, %esi
movl %esi, %r8d
movl %r8d, %r9d
movl %r9d, %r10d
notl %r12d
movl %r12d, %r13d
subl $10, %esi
and $0x80000000, %esi
movl %esi, -16(%ebp)
movl -68(%ebp), %esi
movl %esi, %esi
movl %edi, -20(%ebp)
movl -16(%ebp), %edi
movl %r8d, -24(%ebp)
movl -72(%ebp), %r8d
movl %edi, %r8d
movl %r9d, -28(%ebp)
movl -76(%ebp), %r9d
movl %edi, %r9d
movl %r10d, -32(%ebp)
movl -80(%ebp), %r10d
cmpl %r10d, 0
jne IF1
movl %r12d, -36(%ebp)
movl -84(%ebp), %r12d
movl $1, %r12d
subl %edi, %r12d
movl %r13d, -40(%ebp)
movl -88(%ebp), %r13d
movl %r12d, %r13d
movl %r14d, -44(%ebp)
movl -92(%ebp), %r14d
movl %edi, %r14d
jmp ELSE0
IF1:
movl %r15d, -48(%ebp)
movl -96(%ebp), %r15d
movl $4, %r15d
subl $10, %r15d
and $0x80000000, %r15d
movl %eax, -52(%ebp)
movl -100(%ebp), %eax
movl %r15d, %eax
cmpl %eax, 0
jne IF2
movl %ecx, -56(%ebp)
movl -104(%ebp), %ecx
movl $1, %ecx
movl %edx, -60(%ebp)
movl -108(%ebp), %edx
movl %edi, %edx
jmp LABEL
IF2:
ELSE0:
movl %ebx, -64(%ebp)
movl -112(%ebp), %ebx
movl $1, %ebx
movl %esi, -68(%ebp)
movl -116(%ebp), %esi
movl %edi, -16(%ebp)
movl -120(%ebp), %edi
movl %esi, %edi
movl %r8d, -72(%ebp)
movl -124(%ebp), %r8d
orl $1, %r8d
movl %r9d, -76(%ebp)
movl -128(%ebp), %r9d
movl %r8d, %r9d
movl %r10d, -80(%ebp)
movl -16(%ebp), %r10d
movl %r12d, -84(%ebp)
movl -132(%ebp), %r12d
movl %r10d, %r12d
movl %r13d, -88(%ebp)
movl -136(%ebp), %r13d
movl %r10d, %r13d
movl %r14d, -92(%ebp)
movl -140(%ebp), %r14d
movl %r10d, %r14d
movl %r15d, -96(%ebp)
movl -144(%ebp), %r15d
movl %r10d, %r15d
movl %r15d, %eax
popq %rbp
ret

