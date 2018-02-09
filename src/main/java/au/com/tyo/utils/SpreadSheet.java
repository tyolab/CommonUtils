/*
 * Copyright (c) 2018 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package au.com.tyo.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 7/2/18.
 */

public class SpreadSheet {

    public static final int SPREAD_SHEET_TYPE_CSV = 0;

    public static final int SPREAD_SHEET_TYPE_TSV = 1;

    public static final String SPREAD_SHEET_DELIMITER_TAB = "\\t";
    public static final String SPREAD_SHEET_DELIMITER_COMMA = ",";

    public interface SpreadSheetWatcher {
        void onCell(int r, int c, String data);
        void onEmptyCell(int r, int c);
        void onEmptyRow(int r);
        void onRow(int r, String line);
        int onColumn(int c, String data);
    }

    public static class Cell {
        public int row;
        public int colum;
        public String value;
    }

    private boolean simpleTable;
    private boolean ignoreEmptyCell;
    private boolean firstRowIsHeader;

    private int ignoreRowNonNullColumnsLessThanThisNumber = -1;

    private int type;

    private String delimiter;

    private List<List> table;

    private SpreadSheetWatcher spreadSheetWatcher;

    private int columnCount = 0;

    private List<String> headers;

    public SpreadSheet(int type) {
        this.type = type;

        init();
    }

    private void init() {
        if (type == SPREAD_SHEET_TYPE_TSV)
            delimiter = SPREAD_SHEET_DELIMITER_TAB;
        else if (type == SPREAD_SHEET_TYPE_CSV)
            delimiter = SPREAD_SHEET_DELIMITER_COMMA;
        else
            delimiter = " ";

        simpleTable = false;
        ignoreEmptyCell = true;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public boolean isFirstRowIsHeader() {
        return firstRowIsHeader;
    }

    public void setFirstRowIsHeader(boolean firstRowIsHeader) {
        this.firstRowIsHeader = firstRowIsHeader;
    }

    public int getIgnoreRowNonNullColumnsLessThanThisNumber() {
        return ignoreRowNonNullColumnsLessThanThisNumber;
    }

    public void setIgnoreRowNonNullColumnsLessThanThisNumber(int ignoreRowNonNullColumnsLessThanThisNumber) {
        this.ignoreRowNonNullColumnsLessThanThisNumber = ignoreRowNonNullColumnsLessThanThisNumber;
    }

    public List<List> getTable() {
        return table;
    }

    public int getRowCount() {
        return table == null ? 0 : table.size();
    }

    public int getColumnCount() {
        return columnCount;
    }

    public void setSimpleTable(boolean simpleTable) {
        this.simpleTable = simpleTable;
    }

    public void setIgnoreEmptyCell(boolean ignoreEmptyCell) {
        this.ignoreEmptyCell = ignoreEmptyCell;
    }

    public SpreadSheetWatcher getSpreadSheetWatcher() {
        return spreadSheetWatcher;
    }

    public void setSpreadSheetWatcher(SpreadSheetWatcher spreadSheetWatcher) {
        this.spreadSheetWatcher = spreadSheetWatcher;
    }

    public void createTable(String text) {
        String[] lines = text.split("\n");

        for (int i = 0; i < lines.length; ++i) {
            String line = lines[i];
            line = line.trim();
            List row = null;

            if (line.length() == 0) {
                if (null != spreadSheetWatcher)
                    spreadSheetWatcher.onEmptyRow(i);
                continue;
            }

            if (null != spreadSheetWatcher)
                spreadSheetWatcher.onRow(i, line);
            else
                row = new ArrayList();

            String[] cols = line.split(delimiter);
            String buffer = "";
            int emptyCellCount = 0;

            if (cols.length > 1) {

                // check the column count
                if (cols.length > columnCount)
                    columnCount = cols.length;

                for (int j = 0; j < cols.length; ++j) {
                    String colStr = cols[j];
                    if (null == colStr || (colStr = colStr.trim()).length() == 0) {
                        ++emptyCellCount;

                        if (null != spreadSheetWatcher)
                            spreadSheetWatcher.onEmptyCell(i, j);
                        if (ignoreEmptyCell)
                            continue;
                    }

                    if (null != spreadSheetWatcher)
                        spreadSheetWatcher.onCell(i, j, colStr);
                    else {
                        if (simpleTable) {
                            row.add(colStr);
                        }
                        else {

                            Cell cell = new Cell();
                            cell.row = i;
                            cell.colum = j;
                            cell.value = colStr;

                            row.add(cell);
                        }
                    }
                }
            }

            if (row != null && (row.size() > 0)) {
                if (i == 0 && firstRowIsHeader)
                    headers = row;
                else {
                    if (ignoreRowNonNullColumnsLessThanThisNumber > -1 &&
                            (cols.length < ignoreRowNonNullColumnsLessThanThisNumber ||
                                    ignoreRowNonNullColumnsLessThanThisNumber > (cols.length - emptyCellCount)
                            ))
                        continue;

                    if (table == null)
                        table = new ArrayList<>();
                    table.add(row);
                }
            }
        }
    }

    public void reOrganizeTableWithValidHeaders() {
        Set ignorSet = new HashSet<>();
        List<List> nt = new ArrayList<>();

        // TODO
        // finish it
    }
}
