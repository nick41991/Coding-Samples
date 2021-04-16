	.file	"while.c"
	.text
	.globl	my_func
	.type	my_func, @function
my_func:
.LFB0:
	.cfi_startproc
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset 6, -16
	movq	%rsp, %rbp
	.cfi_def_cfa_register 6
	movl	%edi, -4(%rbp)
	movl	%esi, -8(%rbp)
	movl	-4(%rbp), %eax
	popq	%rbp
	.cfi_def_cfa 7, 8
	ret
	.cfi_endproc
.LFE0:
	.size	my_func, .-my_func
	.globl	main
	.type	main, @function
main:
.LFB1:
	.cfi_startproc
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset 6, -16
	movq	%rsp, %rbp
	.cfi_def_cfa_register 6
	subq	$16, %rsp
	movl	$1, -8(%rbp)
.L4:
	cmpl	$10, -8(%rbp)
	jg	.L5
	addl	$1, -8(%rbp)
	nop
.L5:
	cmpl	$10, -8(%rbp)
	jle	.L6
	cmpl	$5, -8(%rbp)
	je	.L7
.L6:
	movl	-8(%rbp), %edx
	movl	-8(%rbp), %eax
	movl	%edx, %esi
	movl	%eax, %edi
	call	my_func
	cmpl	%eax, -8(%rbp)
	jne	.L8
.L7:
	subl	$1, -8(%rbp)
	jmp	.L9
.L8:
	cmpl	$1, -8(%rbp)
	jne	.L9
	jmp	.L4
.L9:
	movl	$1, -4(%rbp)
	movl	$5, -8(%rbp)
	movl	-4(%rbp), %eax
	leal	1(%rax), %edx
	movl	-8(%rbp), %eax
	movl	%edx, %esi
	movl	%eax, %edi
	call	my_func
	movl	%eax, -8(%rbp)
	movl	-8(%rbp), %eax
	leave
	.cfi_def_cfa 7, 8
	ret
	.cfi_endproc
.LFE1:
	.size	main, .-main
	.ident	"GCC: (Ubuntu 7.5.0-3ubuntu1~18.04) 7.5.0"
	.section	.note.GNU-stack,"",@progbits
