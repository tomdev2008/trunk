package com.example.entity;

import java.io.File;
import java.io.Serializable;


@SuppressWarnings("serial")
public class FormEntity implements Serializable{
    private File formFile;  
    private String formType;
	public FormEntity(File formFile, String formType) {
		super();
		this.formFile = formFile;
		this.formType = formType;
	}
	public File getFormFile() {
		return formFile;
	}
	public void setFormFile(File formFile) {
		this.formFile = formFile;
	}
	public String getFormType() {
		return formType;
	}
	public void setFormType(String formType) {
		this.formType = formType;
	}

}  
