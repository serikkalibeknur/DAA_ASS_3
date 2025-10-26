"""
Chart Generation Script for MST Algorithm Analysis
Generates professional visualizations comparing Prim's and Kruskal's algorithms

Author: Serikkali Beknur
Course: Design and Analysis of Algorithms
Assignment 3: MST Algorithms
"""

import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import numpy as np
import os

# Set style for professional-looking charts
plt.style.use('seaborn-v0_8-darkgrid')
sns.set_palette("husl")

# Create output directory
OUTPUT_DIR = 'charts'
os.makedirs(OUTPUT_DIR, exist_ok=True)

def load_data(csv_file='assign_3_results.csv'):
    """Load and prepare data from CSV file"""
    try:
        df = pd.read_csv(csv_file)
        print(f"✓ Loaded {len(df)} graphs from {csv_file}")
        return df
    except FileNotFoundError:
        print(f"✗ Error: {csv_file} not found!")
        print("  Please run the Java application first to generate results.")
        exit(1)

def categorize_size(vertices):
    """Categorize graph by size"""
    if vertices <= 50:
        return 'Small'
    elif vertices <= 350:
        return 'Medium'
    elif vertices <= 1100:
        return 'Large'
    else:
        return 'Extra Large'

def chart1_execution_time_comparison(df):
    """Chart 1: Execution Time vs Graph Size (Line Chart)"""
    fig, ax = plt.subplots(figsize=(12, 7))
    
    # Plot Prim and Kruskal
    ax.plot(df['Vertices'], df['Prim_Time_ms'], 
            marker='o', linewidth=2.5, markersize=8, 
            label="Prim's Algorithm", color='#e74c3c')
    
    ax.plot(df['Vertices'], df['Kruskal_Time_ms'], 
            marker='s', linewidth=2.5, markersize=8, 
            label="Kruskal's Algorithm", color='#3498db')
    
    ax.set_xlabel('Number of Vertices', fontsize=14, fontweight='bold')
    ax.set_ylabel('Execution Time (ms)', fontsize=14, fontweight='bold')
    ax.set_title('MST Algorithm Performance: Execution Time vs Graph Size', 
                 fontsize=16, fontweight='bold', pad=20)
    ax.legend(fontsize=12, loc='upper left')
    ax.grid(True, alpha=0.3)
    
    # Add annotations for key points
    max_idx = df['Prim_Time_ms'].idxmax()
    ax.annotate(f'Prim: {df.loc[max_idx, "Prim_Time_ms"]:.2f}ms', 
                xy=(df.loc[max_idx, 'Vertices'], df.loc[max_idx, 'Prim_Time_ms']),
                xytext=(10, 20), textcoords='offset points',
                bbox=dict(boxstyle='round,pad=0.5', fc='yellow', alpha=0.7),
                arrowprops=dict(arrowstyle='->', connectionstyle='arc3,rad=0'))
    
    plt.tight_layout()
    filename = os.path.join(OUTPUT_DIR, '1_execution_time_comparison.png')
    plt.savefig(filename, dpi=300, bbox_inches='tight')
    print(f"✓ Created: {filename}")
    plt.close()

def chart2_operations_comparison(df):
    """Chart 2: Operation Count vs Graph Size"""
    fig, ax = plt.subplots(figsize=(12, 7))
    
    ax.plot(df['Vertices'], df['Prim_Operations'], 
            marker='o', linewidth=2.5, markersize=8, 
            label="Prim's Operations", color='#e74c3c')
    
    ax.plot(df['Vertices'], df['Kruskal_Operations'], 
            marker='s', linewidth=2.5, markersize=8, 
            label="Kruskal's Operations", color='#3498db')
    
    ax.set_xlabel('Number of Vertices', fontsize=14, fontweight='bold')
    ax.set_ylabel('Number of Operations', fontsize=14, fontweight='bold')
    ax.set_title('Operation Count Comparison: Prim vs Kruskal', 
                 fontsize=16, fontweight='bold', pad=20)
    ax.legend(fontsize=12, loc='upper left')
    ax.grid(True, alpha=0.3)
    
    # Format y-axis to show thousands
    ax.yaxis.set_major_formatter(plt.FuncFormatter(lambda x, p: f'{int(x/1000)}K' if x >= 1000 else str(int(x))))
    
    plt.tight_layout()
    filename = os.path.join(OUTPUT_DIR, '2_operations_comparison.png')
    plt.savefig(filename, dpi=300, bbox_inches='tight')
    print(f"✓ Created: {filename}")
    plt.close()

def chart3_speedup_analysis(df):
    """Chart 3: Kruskal Speedup over Prim"""
    fig, ax = plt.subplots(figsize=(12, 7))
    
    # Calculate speedup (Prim_Time / Kruskal_Time)
    df['Speedup'] = df['Prim_Time_ms'] / df['Kruskal_Time_ms']
    
    # Color bars based on who wins
    colors = ['#27ae60' if x > 1 else '#e74c3c' for x in df['Speedup']]
    
    bars = ax.bar(range(len(df)), df['Speedup'], color=colors, alpha=0.7, edgecolor='black')
    
    # Add horizontal line at y=1 (equal performance)
    ax.axhline(y=1, color='gray', linestyle='--', linewidth=2, label='Equal Performance')
    
    ax.set_xlabel('Graph ID', fontsize=14, fontweight='bold')
    ax.set_ylabel('Speedup Factor (Prim Time / Kruskal Time)', fontsize=14, fontweight='bold')
    ax.set_title('Kruskal\'s Performance Advantage Over Prim', 
                 fontsize=16, fontweight='bold', pad=20)
    ax.set_xticks(range(0, len(df), 2))
    ax.set_xticklabels(range(1, len(df)+1, 2))
    
    # Add legend
    from matplotlib.patches import Patch
    legend_elements = [
        Patch(facecolor='#27ae60', label='Kruskal Faster'),
        Patch(facecolor='#e74c3c', label='Prim Faster'),
        plt.Line2D([0], [0], color='gray', linestyle='--', linewidth=2, label='Equal Performance')
    ]
    ax.legend(handles=legend_elements, fontsize=12, loc='upper left')
    ax.grid(True, alpha=0.3, axis='y')
    
    plt.tight_layout()
    filename = os.path.join(OUTPUT_DIR, '3_speedup_analysis.png')
    plt.savefig(filename, dpi=300, bbox_inches='tight')
    print(f"✓ Created: {filename}")
    plt.close()

def chart4_performance_by_category(df):
    """Chart 4: Average Performance by Graph Size Category"""
    df['Category'] = df['Vertices'].apply(categorize_size)
    
    # Calculate averages by category
    category_stats = df.groupby('Category').agg({
        'Prim_Time_ms': 'mean',
        'Kruskal_Time_ms': 'mean',
        'Vertices': 'mean'
    }).reset_index()
    
    # Sort by vertices
    category_stats = category_stats.sort_values('Vertices')
    
    fig, ax = plt.subplots(figsize=(12, 7))
    
    x = np.arange(len(category_stats))
    width = 0.35
    
    bars1 = ax.bar(x - width/2, category_stats['Prim_Time_ms'], width, 
                   label="Prim's Algorithm", color='#e74c3c', alpha=0.8, edgecolor='black')
    bars2 = ax.bar(x + width/2, category_stats['Kruskal_Time_ms'], width, 
                   label="Kruskal's Algorithm", color='#3498db', alpha=0.8, edgecolor='black')
    
    # Add value labels on bars
    for bars in [bars1, bars2]:
        for bar in bars:
            height = bar.get_height()
            ax.text(bar.get_x() + bar.get_width()/2., height,
                   f'{height:.2f}ms',
                   ha='center', va='bottom', fontsize=10, fontweight='bold')
    
    ax.set_xlabel('Graph Size Category', fontsize=14, fontweight='bold')
    ax.set_ylabel('Average Execution Time (ms)', fontsize=14, fontweight='bold')
    ax.set_title('Average Performance by Graph Size Category', 
                 fontsize=16, fontweight='bold', pad=20)
    ax.set_xticks(x)
    ax.set_xticklabels(category_stats['Category'])
    ax.legend(fontsize=12)
    ax.grid(True, alpha=0.3, axis='y')
    
    plt.tight_layout()
    filename = os.path.join(OUTPUT_DIR, '4_performance_by_category.png')
    plt.savefig(filename, dpi=300, bbox_inches='tight')
    print(f"✓ Created: {filename}")
    plt.close()

def chart5_density_impact(df):
    """Chart 5: Impact of Graph Density on Performance"""
    # Calculate density (E / max_possible_edges)
    df['Density'] = df['Edges'] / (df['Vertices'] * (df['Vertices'] - 1) / 2)
    
    fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(16, 7))
    
    # Scatter plot: Density vs Time
    ax1.scatter(df['Density'], df['Prim_Time_ms'], 
               s=100, alpha=0.6, label="Prim's Algorithm", 
               color='#e74c3c', edgecolors='black', linewidth=1.5)
    ax1.scatter(df['Density'], df['Kruskal_Time_ms'], 
               s=100, alpha=0.6, label="Kruskal's Algorithm", 
               color='#3498db', edgecolors='black', linewidth=1.5)
    
    ax1.set_xlabel('Graph Density (E / Max Edges)', fontsize=12, fontweight='bold')
    ax1.set_ylabel('Execution Time (ms)', fontsize=12, fontweight='bold')
    ax1.set_title('Execution Time vs Graph Density', fontsize=14, fontweight='bold')
    ax1.legend(fontsize=10)
    ax1.grid(True, alpha=0.3)
    
    # Scatter plot: Density vs Speedup
    df['Speedup'] = df['Prim_Time_ms'] / df['Kruskal_Time_ms']
    ax2.scatter(df['Density'], df['Speedup'], 
               s=100, alpha=0.6, color='#27ae60', 
               edgecolors='black', linewidth=1.5)
    ax2.axhline(y=1, color='red', linestyle='--', linewidth=2, label='Equal Performance')
    
    ax2.set_xlabel('Graph Density (E / Max Edges)', fontsize=12, fontweight='bold')
    ax2.set_ylabel('Speedup (Prim / Kruskal)', fontsize=12, fontweight='bold')
    ax2.set_title('Kruskal Advantage vs Graph Density', fontsize=14, fontweight='bold')
    ax2.legend(fontsize=10)
    ax2.grid(True, alpha=0.3)
    
    plt.tight_layout()
    filename = os.path.join(OUTPUT_DIR, '5_density_impact.png')
    plt.savefig(filename, dpi=300, bbox_inches='tight')
    print(f"✓ Created: {filename}")
    plt.close()

def chart6_winner_distribution(df):
    """Chart 6: Winner Distribution Pie Chart"""
    # Determine winner for each graph
    df['Winner'] = df.apply(lambda row: 
        'Prim' if row['Prim_Time_ms'] < row['Kruskal_Time_ms'] * 0.99
        else ('Kruskal' if row['Kruskal_Time_ms'] < row['Prim_Time_ms'] * 0.99
              else 'Tie'), axis=1)
    
    winner_counts = df['Winner'].value_counts()
    
    fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(16, 7))
    
    # Pie chart
    colors = ['#3498db', '#e74c3c', '#95a5a6']
    explode = (0.1, 0, 0) if 'Kruskal' in winner_counts.index else (0, 0, 0)
    
    wedges, texts, autotexts = ax1.pie(winner_counts, labels=winner_counts.index, 
                                         autopct='%1.1f%%', startangle=90,
                                         colors=colors[:len(winner_counts)],
                                         explode=explode[:len(winner_counts)],
                                         shadow=True, textprops={'fontsize': 12, 'fontweight': 'bold'})
    
    ax1.set_title('Overall Winner Distribution\n(28 Test Graphs)', 
                  fontsize=14, fontweight='bold')
    
    # Bar chart by category
    df['Category'] = df['Vertices'].apply(categorize_size)
    category_winners = df.groupby(['Category', 'Winner']).size().unstack(fill_value=0)
    category_winners = category_winners.reindex(['Small', 'Medium', 'Large', 'Extra Large'])
    
    category_winners.plot(kind='bar', ax=ax2, color=['#3498db', '#e74c3c', '#95a5a6'][:len(category_winners.columns)],
                          alpha=0.8, edgecolor='black', linewidth=1.5)
    
    ax2.set_xlabel('Graph Size Category', fontsize=12, fontweight='bold')
    ax2.set_ylabel('Number of Wins', fontsize=12, fontweight='bold')
    ax2.set_title('Winner Distribution by Category', fontsize=14, fontweight='bold')
    ax2.legend(title='Winner', fontsize=10)
    ax2.set_xticklabels(ax2.get_xticklabels(), rotation=45, ha='right')
    ax2.grid(True, alpha=0.3, axis='y')
    
    plt.tight_layout()
    filename = os.path.join(OUTPUT_DIR, '6_winner_distribution.png')
    plt.savefig(filename, dpi=300, bbox_inches='tight')
    print(f"✓ Created: {filename}")
    plt.close()

def chart7_cost_verification(df):
    """Chart 7: Cost Verification (Both algorithms should match)"""
    fig, ax = plt.subplots(figsize=(12, 7))
    
    x = range(len(df))
    
    # Plot both costs - they should overlap perfectly
    ax.plot(x, df['Prim_Cost'], marker='o', linewidth=2, markersize=6,
            label="Prim's MST Cost", color='#e74c3c', alpha=0.7)
    ax.plot(x, df['Kruskal_Cost'], marker='s', linewidth=2, markersize=6,
            label="Kruskal's MST Cost", color='#3498db', alpha=0.7, linestyle='--')
    
    ax.set_xlabel('Graph ID', fontsize=14, fontweight='bold')
    ax.set_ylabel('MST Total Cost', fontsize=14, fontweight='bold')
    ax.set_title('MST Cost Verification: Both Algorithms Should Produce Identical Costs', 
                 fontsize=16, fontweight='bold', pad=20)
    ax.legend(fontsize=12)
    ax.grid(True, alpha=0.3)
    ax.set_xticks(range(0, len(df), 2))
    ax.set_xticklabels(range(1, len(df)+1, 2))
    
    # Check if all costs match
    all_match = (df['Prim_Cost'] == df['Kruskal_Cost']).all()
    match_text = "✓ All costs match perfectly!" if all_match else "✗ Warning: Some costs don't match!"
    color = 'green' if all_match else 'red'
    
    ax.text(0.5, 0.95, match_text, transform=ax.transAxes,
            fontsize=14, fontweight='bold', color=color,
            ha='center', va='top',
            bbox=dict(boxstyle='round', facecolor='wheat', alpha=0.8))
    
    plt.tight_layout()
    filename = os.path.join(OUTPUT_DIR, '7_cost_verification.png')
    plt.savefig(filename, dpi=300, bbox_inches='tight')
    print(f"✓ Created: {filename}")
    plt.close()

def generate_summary_report(df):
    """Generate text summary of findings"""
    summary_file = os.path.join(OUTPUT_DIR, 'analysis_summary.txt')
    
    with open(summary_file, 'w', encoding='utf-8') as f:
        f.write("=" * 80 + "\n")
        f.write("MST ALGORITHM COMPARISON - ANALYSIS SUMMARY\n")
        f.write("=" * 80 + "\n\n")
        
        # Overall statistics
        f.write("OVERALL STATISTICS\n")
        f.write("-" * 80 + "\n")
        f.write(f"Total graphs tested: {len(df)}\n")
        f.write(f"Prim total time: {df['Prim_Time_ms'].sum():.2f} ms\n")
        f.write(f"Kruskal total time: {df['Kruskal_Time_ms'].sum():.2f} ms\n")
        f.write(f"Average speedup (Kruskal): {(df['Prim_Time_ms'].sum() / df['Kruskal_Time_ms'].sum()):.2f}x\n\n")
        
        # Winner count
        df['Winner'] = df.apply(lambda row: 
            'Prim' if row['Prim_Time_ms'] < row['Kruskal_Time_ms'] * 0.99
            else ('Kruskal' if row['Kruskal_Time_ms'] < row['Prim_Time_ms'] * 0.99
                  else 'Tie'), axis=1)
        
        winner_counts = df['Winner'].value_counts()
        f.write("WINNER DISTRIBUTION\n")
        f.write("-" * 80 + "\n")
        for winner, count in winner_counts.items():
            percentage = (count / len(df)) * 100
            f.write(f"{winner}: {count} wins ({percentage:.1f}%)\n")
        f.write("\n")
        
        # By category
        df['Category'] = df['Vertices'].apply(categorize_size)
        f.write("PERFORMANCE BY CATEGORY\n")
        f.write("-" * 80 + "\n")
        for category in ['Small', 'Medium', 'Large', 'Extra Large']:
            cat_df = df[df['Category'] == category]
            if len(cat_df) > 0:
                avg_prim = cat_df['Prim_Time_ms'].mean()
                avg_kruskal = cat_df['Kruskal_Time_ms'].mean()
                speedup = avg_prim / avg_kruskal
                f.write(f"\n{category} graphs ({len(cat_df)} graphs):\n")
                f.write(f"  Average Prim time: {avg_prim:.2f} ms\n")
                f.write(f"  Average Kruskal time: {avg_kruskal:.2f} ms\n")
                f.write(f"  Speedup: {speedup:.2f}x\n")
        
        f.write("\n" + "=" * 80 + "\n")
        f.write("END OF SUMMARY\n")
        f.write("=" * 80 + "\n")
    
    print(f"✓ Created: {summary_file}")

def main():
    """Main execution function"""
    print("\n" + "=" * 80)
    print("MST ALGORITHM ANALYSIS - CHART GENERATION")
    print("=" * 80 + "\n")
    
    # Load data
    df = load_data()
    
    print(f"\nGenerating professional charts...")
    print("-" * 80)
    
    # Generate all charts
    chart1_execution_time_comparison(df)
    chart2_operations_comparison(df)
    chart3_speedup_analysis(df)
    chart4_performance_by_category(df)
    chart5_density_impact(df)
    chart6_winner_distribution(df)
    chart7_cost_verification(df)
    
    # Generate summary
    generate_summary_report(df)
    
    print("\n" + "=" * 80)
    print("✓ ALL CHARTS GENERATED SUCCESSFULLY!")
    print("=" * 80)
    print(f"\nCharts saved in: {OUTPUT_DIR}/")
    print("\nGenerated files:")
    print("  1. 1_execution_time_comparison.png")
    print("  2. 2_operations_comparison.png")
    print("  3. 3_speedup_analysis.png")
    print("  4. 4_performance_by_category.png")
    print("  5. 5_density_impact.png")
    print("  6. 6_winner_distribution.png")
    print("  7. 7_cost_verification.png")
    print("  8. analysis_summary.txt")
    print("\n✓ Ready for report inclusion!\n")

if __name__ == "__main__":
    main()

