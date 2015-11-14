package com.flower.admin.beans;

import java.util.List;

public class PagerInfo {

	private int pageCount;
	private int firstPageNumber;
	private int endPageNumber;
	private int pageDisplayCount;
	private List dataList; 
	private int currentPage;
	public static final int pageSize =10;

	public PagerInfo()
	{
		this.pageDisplayCount = 10;
	}
	
	public void CaclePager(int ResultCount,int PageSize,int CurrentPage)
	{
		this.pageCount = ResultCount / PageSize;
		if(ResultCount%PageSize != 0) this.pageCount++;
		
		this.firstPageNumber = 1;
		
		if(CurrentPage > this.pageCount)
			this.currentPage = this.pageCount;
		else
			this.currentPage = CurrentPage;
		
		if(this.currentPage == 0)
			this.currentPage = 1;
		
		firstPageNumber = this.currentPage-5;
		if(firstPageNumber<=0)
			firstPageNumber = 1;
		
		endPageNumber = firstPageNumber + 10;
		if(endPageNumber > pageCount)
			endPageNumber = pageCount;
		
		if(pageDisplayCount > pageCount)
		{
			pageDisplayCount = pageCount;
		}
	}
	
	public int getCurrentPage()
	{
		return this.currentPage;
	}
	
	public void setDataList(List list)
	{
		this.dataList = list;
	}
	
	public int getPageCount()
	{
		return this.pageCount;
	}
	
	public List getDataList()
	{
		return this.dataList;
	}
	
	
	public int getPageDisplayCount()
	{
		return this.pageDisplayCount;
	}
	
	public int getFirstPageNumber()
	{
		return this.firstPageNumber;
	}
	
	public int getEndPageNumber()
	{
		return this.endPageNumber;
	}
	
}
