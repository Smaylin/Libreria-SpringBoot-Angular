import { Component } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { BookService } from '../services/book.service';
import { Button, ButtonModule } from 'primeng/button';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { MessageService } from 'primeng/api';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { CardModule } from 'primeng/card';
import { FileSelectEvent } from 'primeng/fileupload';
import { FileUploadModule } from 'primeng/fileupload';


@Component({
  selector: 'app-book-form',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    FormsModule,
    ButtonModule,
    RouterModule,
    InputTextModule,
    InputNumberModule,
    CardModule,
    FileUploadModule
  ],
  templateUrl: './book-form.component.html',
  styleUrl: './book-form.component.scss'
})
export class BookFormComponent {
  formBook!: FormGroup
  isSaveInProgress: boolean = false; // Flag to indicate if the save operation is in progress
  editMode: boolean = false; // Flag to indicate if the form is in edit mode
  selectedFile:File | null = null; // Variable to store the selected file

  constructor(
    private fb: FormBuilder, 
    private bookService: BookService, 
    private activatedRoute: ActivatedRoute,
    private messageService: MessageService,
    private router: Router
  ) {

    this.formBook = this.fb.group({
      id: [null],
      title: ['', Validators.required],
      author: ['', Validators.required],
      price: [0, [Validators.required, Validators.min(0)]],
      pages: [1, [Validators.required, Validators.min(1)]]
    });
    
  }

  ngOnInit(): void {
    let id = this.activatedRoute.snapshot.paramMap.get('id');
    if(id !='new'){
      this.editMode = true;
      this.getBookById(+id!);
    }
  }

  onFileSelected(event:FileSelectEvent){
    this.selectedFile = event.files[0];
  }

  getBookById(id: number){
    this.bookService.getBookById(id).subscribe({
      next:foundBook=>{
        this.formBook.patchValue(foundBook);
    },
    error:()=>{
      this.messageService.add({
        severity:'error',
        summary:'Error',
        detail:'Book not found'
      });
      this.router.navigateByUrl('/');
    }
  });
}
    createBook(){
      if(this.formBook.invalid){
        this.messageService.add({
          severity:'error',
          summary:'Error',
          detail:'Please fill all fields'
        });
        return;
      }
      if(this.selectedFile == null){
        this.messageService.add({
          severity:'error',
          summary:'Error',
          detail:'Please select an image'
        });
        return;
      }
      this.isSaveInProgress = true;
      this.bookService.createBook(this.formBook.value, this.selectedFile).subscribe({
        next:()=>{
          this.messageService.add({
            severity:'success',
            summary:'Success',
            detail:'Book created'
          });
          this.isSaveInProgress = false; // Reset the flag
          this.router.navigateByUrl('/');
        },
        error:()=>{
          this.messageService.add({
            severity:'error',
            summary:'Error',
            detail:'Error creating book'
          });
        }
      });
    }

    changeImage(event:FileSelectEvent){
      this.selectedFile = event.files[0];
      if(!this.selectedFile == null){
        this.messageService.add({
          severity:'error',
          summary:'Error',
          detail:'Please select an image'
        });
        return;
      }
      this.bookService.upadteBookImage(this.formBook.value.id, this.selectedFile).subscribe({
        next:()=>{
          this.messageService.add({
            severity:'success',
            summary:'Success',
            detail:'Image updated'
          });
          this.isSaveInProgress = false;
          this.router.navigateByUrl('/');
        },
        error:()=>{
          this.messageService.add({
            severity:'error',
            summary:'Error',
            detail:'Error updating image'
          });
        }
      });
    }


updateBook() {
    if (this.formBook.invalid) {
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: 'Revise los campos e intente nuevamente',
      });
      return;
    }
    this.isSaveInProgress = true;
    this.bookService.updateBook(this.formBook.value).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Guardado',
          detail: 'Libro actualizado correctamente',
        });
        this.isSaveInProgress = false;
        this.router.navigateByUrl('/');
      },
      error: () => {
        this.isSaveInProgress = false;
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Revise los campos e intente nuevamente',
        });
      },
    });
  }
}
