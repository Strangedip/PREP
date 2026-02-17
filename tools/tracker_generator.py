import pandas as pd
import io
import datetime
import xlsxwriter.utility

# --- CONFIGURATION ---
filename = 'Tracking_Iron_Protocol.xlsx'
start_date = datetime.date(2026, 2, 20)
end_date = datetime.date(2026, 4, 21)

# Generate Dates
delta = end_date - start_date
dates = [start_date + datetime.timedelta(days=i) for i in range(delta.days + 1)]
days_to_track = len(dates)

# THE ROUTINE (Gym is now a standard "Check" column)
routine_cols = [
    ("⏰ Wake 7am", "Check"),
    ("🏋️ Gym (2hr)", "Check"),  # CHANGED to Check
    ("🧠 Prep (2hr)", "Check"),
    ("💼 Job (9hr)", "Check"),
    ("🎧 Audio (1hr)", "Check"),
    ("💻 Prep (1hr)", "Check"),
    ("🚶 Walking", "Check"),
    ("🥗 Protein", "Check"),
    ("❌ No Junk", "Check"),
    ("💤 Sleep 12am", "Check")
]

# --- DATAFRAME SETUP ---
data = {
    "DATE": [d.strftime("%d-%b") for d in dates],
    "DAY": [d.strftime("%a") for d in dates],
    "🔥 WIN %": [""] * days_to_track,
}
for col, _ in routine_cols:
    data[col] = [""] * days_to_track
data["📝 NOTES"] = [""] * days_to_track
df = pd.DataFrame(data)

# --- EXCEL GENERATION ---
output = io.BytesIO()
writer = pd.ExcelWriter(filename, engine='xlsxwriter')

# INDICES (0-based)
ROW_HIDDEN_CALC = 6      # Excel Row 7
ROW_HEADERS = 8          # Excel Row 9
ROW_DATA_START = 9       # Excel Row 10 (First Date)
ROW_DATA_END = ROW_DATA_START + days_to_track - 1
START_COL_IDX = 3        # Routine starts at Column D

df.to_excel(writer, sheet_name='Tracker', startrow=ROW_HEADERS, index=False)

workbook = writer.book
worksheet = writer.sheets['Tracker']
worksheet.hide_gridlines(2)

# --- STYLES ---
C_DARK = '#0F172A'
C_ACCENT = '#3B82F6'
C_GREEN_BG = '#DCFCE7'
C_GREEN_TXT = '#166534'
C_RED_BG = '#FEE2E2'
C_RED_TXT = '#991B1B'

fmt_header = workbook.add_format({
    'bold': True, 'font_color': 'white', 'bg_color': C_DARK,
    'align': 'center', 'valign': 'vcenter', 'border': 1, 'border_color': C_ACCENT,
    'text_wrap': True, 'font_size': 9
})
fmt_date = workbook.add_format({
    'num_format': 'dd-mmm', 'align': 'center', 'valign': 'vcenter',
    'font_color': C_ACCENT, 'bold': True, 'border': 1, 'border_color': '#E2E8F0'
})
fmt_day = workbook.add_format({
    'align': 'center', 'valign': 'vcenter', 'font_color': '#64748B',
    'border': 1, 'border_color': '#E2E8F0', 'font_size': 9
})
fmt_progress = workbook.add_format({
    'num_format': '0%', 'align': 'center', 'valign': 'vcenter', 
    'bold': True, 'border': 1, 'border_color': '#E2E8F0', 'bg_color': '#F8FAFC'
})
fmt_center = workbook.add_format({'align': 'center', 'valign': 'vcenter', 'border': 1, 'border_color': '#E2E8F0'})

# --- DASHBOARD (FIXED MERGES) ---
worksheet.merge_range('B2:E2', "🛡️ IRON PROTOCOL: OFFICIAL DASHBOARD", 
                      workbook.add_format({'bold': True, 'font_size': 16, 'font_color': C_DARK}))
worksheet.write('B3', f"{start_date.strftime('%d %b')} - {end_date.strftime('%d %b')}", 
                workbook.add_format({'font_color': '#64748B', 'bold': True}))

kpi_label = workbook.add_format({'font_size': 9, 'bold': True, 'font_color': '#64748B', 'align': 'left'})
kpi_value = workbook.add_format({'font_size': 22, 'bold': True, 'font_color': C_DARK, 'align': 'left'})
kpi_score = workbook.add_format({'font_size': 24, 'bold': True, 'font_color': C_ACCENT, 'num_format': '0%', 'align': 'left'})
kpi_habit = workbook.add_format({'font_size': 20, 'bold': True, 'font_color': '#16A34A', 'align': 'left'})

# 1. Consistency Score
worksheet.merge_range('B5:C5', "PROTOCOL ADHERENCE", kpi_label)
worksheet.merge_range('B6:C6', "", kpi_score)
worksheet.write_formula('B6', f'=AVERAGE(C{ROW_DATA_START+1}:C{ROW_DATA_END+1})', kpi_score)

# 2. Deep Work (Calculated from Checks)
worksheet.merge_range('E5:F5', "DEEP WORK HOURS", kpi_label)
worksheet.merge_range('E6:F6', "", kpi_value)
# Prep 2hr is Index 2 (Col F). Prep 1hr is Index 5 (Col I).
worksheet.write_formula('E6', 
    f'=(COUNTIF(F{ROW_DATA_START+1}:F{ROW_DATA_END+1}, "✓")*2) + (COUNTIF(I{ROW_DATA_START+1}:I{ROW_DATA_END+1}, "✓")*1)', 
    kpi_value)

# 3. Strongest Habit
worksheet.merge_range('H5:J5', "🏆 STRONGEST HABIT", kpi_label)
worksheet.merge_range('H6:J6', "", kpi_habit)
worksheet.write_formula('H6', f'=INDEX(D9:M9, MATCH(MAX(D7:M7), D7:M7, 0))', kpi_habit)

# --- HIDDEN CALCULATIONS ---
worksheet.set_row(ROW_HIDDEN_CALC, None, None, {'hidden': True})
calc_range_len = days_to_track
for i, (name, col_type) in enumerate(routine_cols):
    col_idx = START_COL_IDX + i
    col_char = xlsxwriter.utility.xl_col_to_name(col_idx)
    range_str = f'{col_char}{ROW_DATA_START+1}:{col_char}{ROW_DATA_END+1}'
    # Simplified logic: Just count checks for everything
    worksheet.write_formula(ROW_HIDDEN_CALC, col_idx, f'=COUNTIF({range_str}, "✓")/{calc_range_len}')

# --- HEADERS & COLUMN WIDTHS ---
for col_num, value in enumerate(df.columns.values):
    worksheet.write(ROW_HEADERS, col_num, value, fmt_header)

worksheet.set_column('A:A', 10, fmt_date)
worksheet.set_column('B:B', 6, fmt_day)
worksheet.set_column('C:C', 10, fmt_progress)
worksheet.set_column(START_COL_IDX, START_COL_IDX + len(routine_cols) - 1, 11, fmt_center)
worksheet.set_column(START_COL_IDX + len(routine_cols), START_COL_IDX + len(routine_cols), 30, fmt_center)

# --- DATA VALIDATION (ALL YES/NO) ---
for i, (name, col_type) in enumerate(routine_cols):
    col_idx = START_COL_IDX + i
    worksheet.data_validation(ROW_DATA_START, col_idx, ROW_DATA_END, col_idx, {
        'validate': 'list', 'source': ['✓', 'x']
    })

# --- ROW LOGIC (FORMULAS) ---
start_char = xlsxwriter.utility.xl_col_to_name(START_COL_IDX)
end_char = xlsxwriter.utility.xl_col_to_name(START_COL_IDX + len(routine_cols) - 1)

for r in range(ROW_DATA_START, ROW_DATA_END + 1):
    row_num = r + 1
    # Simplified Formula: Just count "✓" across the whole row
    formula = f'=(COUNTIF({start_char}{row_num}:{end_char}{row_num}, "✓")) / {len(routine_cols)}'
    worksheet.write_formula(r, 2, formula, fmt_progress)

# --- CONDITIONAL FORMATTING ---
# 1. Progress Bar
worksheet.conditional_format(ROW_DATA_START, 2, ROW_DATA_END, 2, {
    'type': 'data_bar', 'bar_color': '#3B82F6', 'min_type': 'num', 'min_value': 0, 'max_type': 'num', 'max_value': 1
})
# 2. Success (Green) - For Checks
worksheet.conditional_format(ROW_DATA_START, START_COL_IDX, ROW_DATA_END, START_COL_IDX + len(routine_cols) - 1, {
    'type': 'cell', 'criteria': 'equal to', 'value': '"✓"', 
    'format': workbook.add_format({'bg_color': C_GREEN_BG, 'font_color': C_GREEN_TXT, 'bold': True})
})
# 3. Failure (Red) - For 'x'
worksheet.conditional_format(ROW_DATA_START, START_COL_IDX, ROW_DATA_END, START_COL_IDX + len(routine_cols) - 1, {
    'type': 'cell', 'criteria': 'equal to', 'value': '"x"',
    'format': workbook.add_format({'bg_color': C_RED_BG, 'font_color': C_RED_TXT})
})

writer.close()
print(f"File '{filename}' generated successfully.")