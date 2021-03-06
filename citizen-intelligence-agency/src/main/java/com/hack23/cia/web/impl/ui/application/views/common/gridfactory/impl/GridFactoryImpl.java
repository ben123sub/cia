/*
 * Copyright 2014 James Pether Sörling
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *	$Id$
 *  $HeadURL$
*/
package com.hack23.cia.web.impl.ui.application.views.common.gridfactory.impl;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;

import com.hack23.cia.web.impl.ui.application.views.common.converters.ListPropertyConverter;
import com.hack23.cia.web.impl.ui.application.views.common.gridfactory.api.GridFactory;
import com.hack23.cia.web.impl.ui.application.views.common.sizing.ContentRatio;
import com.hack23.cia.web.impl.ui.application.views.pageclicklistener.AbstractPageItemRendererClickListener;
import com.vaadin.data.BeanPropertySet;
import com.vaadin.data.PropertySet;
import com.vaadin.data.ValueProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.event.selection.SelectionListener;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;

/**
 * The Class GridFactoryImpl.
 */
@Service
public final class GridFactoryImpl implements GridFactory {

	/**
	 * Instantiates a new grid factory impl.
	 */
	public GridFactoryImpl() {
		super();
	}

	@Override
	public <T extends Serializable> void createBasicBeanItemGrid(final AbstractOrderedLayout panelContent,final Class<T> dataType, final List<T> datasource,
			final String caption, final String[] columnOrder, final String[] hideColumns,
			final AbstractPageItemRendererClickListener<?> listener, final String actionId, final ListPropertyConverter[] collectionPropertyConverters) {
		createBasicBeanItemNestedPropertiesGrid(panelContent,dataType,datasource, caption, null, columnOrder, hideColumns, listener, actionId, collectionPropertyConverters);


	}

	@Override
	public <T extends Serializable> void createBasicBeanItemNestedPropertiesGrid(final AbstractOrderedLayout panelContent,final Class<T> dataType, final List<T> datasource, final String caption, final String[] nestedProperties,
			final String[] columnOrder, final String[] hideColumns, final AbstractPageItemRendererClickListener<?> listener,
			final String actionId, final ListPropertyConverter[] collectionPropertyConverters) {

		final Grid<T> grid = new Grid<T>(caption).withPropertySet(BeanPropertySet.get(dataType));
		
		grid.setItems(datasource.stream().filter(party -> party != null) 
        .collect(Collectors.toList()));
			
		grid.setSelectionMode(SelectionMode.SINGLE);

		createNestedProperties(grid, nestedProperties);

		configureColumnOrdersAndHiddenFields(columnOrder, hideColumns, grid);

		configureListeners(listener, grid);

		setColumnConverters(collectionPropertyConverters, grid);

		grid.setSizeFull();

		grid.setStyleName("Level2Header");

		createGridCellFilter(columnOrder, grid,dataType);

		grid.setResponsive(true);

		panelContent.addComponent(grid);
		panelContent.setExpandRatio(grid, ContentRatio.GRID);
	}

	private <T extends Serializable> void createNestedProperties(Grid<T> grid, String[] nestedProperties) {
		if (nestedProperties != null) {
			for (String property : nestedProperties) {			
				Column<T, ?> addColumn = grid.addColumn(new BeanNestedPropertyValueProvider<T>(property));
				addColumn.setId(property);
			}
		}		
	}
	
	public class BeanNestedPropertyValueProvider<T> implements ValueProvider<T, String> {

		private final String property;
				
		public BeanNestedPropertyValueProvider(String property) {
			super();
			this.property = property;
		}
		
		@Override
		public String apply(T source) {
			try {
				return BeanUtils.getProperty(source, property);
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				e.printStackTrace();
				return "";
			}
		}
		
	}

	/**
	 * Configure column orders and hidden fields.
	 *
	 * @param columnOrder
	 *            the column order
	 * @param hideColumns
	 *            the hide columns
	 * @param grid
	 *            the grid
	 */
	private static void configureColumnOrdersAndHiddenFields(final String[] columnOrder, final String[] hideColumns,
			final Grid grid) {
		if (columnOrder != null) {
			grid.setColumnOrder(columnOrder);
		}

		if (hideColumns != null) {
			for (final String o : hideColumns) {
				grid.removeColumn(o);
			}
		}
	}

	/**
	 * Configure listeners.
	 *
	 * @param listener
	 *            the listener
	 * @param grid
	 *            the grid
	 */
	private static void configureListeners(final SelectionListener listener,
			final Grid grid) {

		if (listener != null) {
			grid.addSelectionListener(listener);
		}
	}


	/**
	 * Creates the grid cell filter.
	 *
	 * @param columnOrder
	 *            the column order
	 * @param grid
	 *            the grid
	 */
	/**
	 * Creates the grid cell filter.
	 *
	 * @param columnOrder
	 *            the column order
	 * @param grid
	 *            the grid
	 */
	private static void createGridCellFilter(final String[] columnOrder, final Grid grid,final Class dataType) {
		if (columnOrder != null) {
//			final GridCellFilter gridCellFilter = new GridCellFilter(grid, dataType);
//			for (final String column : columnOrder) {
//				if (grid.getColumn(column) != null) {
//					gridCellFilter.setTextFilter(column, true, true);
//				}
//			}
		}
	}


	/**
	 * Sets the column converters.
	 *
	 * @param collectionPropertyConverter
	 *            the collection property converter
	 * @param grid
	 *            the grid
	 */
	private static void setColumnConverters(final ListPropertyConverter[] collectionPropertyConverter, final Grid grid) {
		if (collectionPropertyConverter != null) {
			for (final ListPropertyConverter converter : collectionPropertyConverter) {
				grid.getColumn(converter.getColumn());

			}
		}
	}

}
